/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors

import com.google.inject.{ImplementedBy, Inject}
import play.api.libs.json.Format
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.repositories.SessionRepository

import scala.concurrent.{ExecutionContext, Future}

class DataCacheConnectorImpl @Inject()(
                                        sessionRepository: SessionRepository
                                      )(implicit ec: ExecutionContext)
  extends DataCacheConnector {

  def save[A](cacheId: String, key: String, value: A)(implicit fmt: Format[A]): Future[CacheMap] =
    sessionRepository.save(cacheId, key, value)

  def remove(cacheId: String, key: String): Future[Boolean] =
    sessionRepository.remove(cacheId, key)

  def clear(cacheId: String): Future[Boolean] =
    sessionRepository.removeEntity(cacheId)

  def fetch(cacheId: String): Future[Option[CacheMap]] =
    sessionRepository.findEntity(cacheId)

  def getEntry[A](cacheId: String, key: String)(implicit fmt: Format[A]): Future[Option[A]] =
    sessionRepository.get(cacheId, key)

  def addToCollection[A](cacheId: String, collectionKey: String, value: A)(implicit fmt: Format[A]): Future[CacheMap] =
    getEntry[Seq[A]](cacheId, collectionKey)
      .flatMap(valuesOpt => save(cacheId, collectionKey, add(valuesOpt, value)))

  def removeFromCollection[A](cacheId: String, collectionKey: String, item: A)(implicit fmt: Format[A]): Future[CacheMap] =
    getEntry[Seq[A]](cacheId, collectionKey)
      .flatMap(
        remove(_, item) match {
          case Seq() =>
            remove(cacheId, collectionKey)
            fetch(cacheId).map(_.getOrElse(throw new Exception(s"Couldn't find document with key $cacheId")))
          case newSeq =>
            save(cacheId, collectionKey, newSeq)
        }
      )

  def replaceInCollection[A](cacheId: String, collectionKey: String, index: Int, item: A)(implicit fmt: Format[A]): Future[CacheMap] =
    getEntry[Seq[A]](cacheId, collectionKey)
      .flatMap(valuesOpt => save(cacheId, collectionKey, replace(valuesOpt, index, item)))

  private def add[A](valuesOpt: Option[Seq[A]], value: A): Seq[A] =
    valuesOpt.getOrElse(Seq()) :+ value

  private def remove[A](valuesOpt: Option[Seq[A]], value: A): Seq[A] =
    valuesOpt.getOrElse(Seq()).filterNot(_ == value)

  private def replace[A](valuesOpt: Option[Seq[A]], index: Int, value: A): Seq[A] =
    valuesOpt.getOrElse(Seq()).updated(index, value)

}

@ImplementedBy(classOf[DataCacheConnectorImpl])
trait DataCacheConnector {
  def save[A](cacheId: String, key: String, value: A)(implicit fmt: Format[A]): Future[CacheMap]

  def remove(cacheId: String, key: String): Future[Boolean]

  def clear(cacheId: String): Future[Boolean]

  def fetch(cacheId: String): Future[Option[CacheMap]]

  def getEntry[A](cacheId: String, key: String)(implicit fmt: Format[A]): Future[Option[A]]

  def addToCollection[A](cacheId: String, collectionKey: String, value: A)(implicit fmt: Format[A]): Future[CacheMap]

  def removeFromCollection[A](cacheId: String, collectionKey: String, item: A)(implicit fmt: Format[A]): Future[CacheMap]

  def replaceInCollection[A](cacheId: String, collectionKey: String, index: Int, item: A)(implicit fmt: Format[A]): Future[CacheMap]
}
