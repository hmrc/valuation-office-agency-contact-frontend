/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.repositories

import play.api.libs.json.{Reads, Writes}
import play.api.{Configuration, Logging}
import uk.gov.hmrc.mongo.cache.CacheIdType.SimpleCacheId
import uk.gov.hmrc.mongo.cache.{CacheItem, DataKey, MongoCacheRepository}
import uk.gov.hmrc.mongo.{MongoComponent, TimestampSupport}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.CacheMap

import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SessionRepository @Inject() (
  config: Configuration,
  mongo: MongoComponent,
  timestampSupport: TimestampSupport
)(implicit ec: ExecutionContext
) extends MongoCacheRepository(
    mongoComponent = mongo,
    collectionName = "sessions",
    ttl = Duration(config.get[Long]("mongodb.timeToLiveInSeconds"), SECONDS),
    timestampSupport = timestampSupport,
    cacheIdType = SimpleCacheId
  )
  with Logging {

  implicit class cacheItemOps(cacheItem: CacheItem) {
    def asCacheMap: CacheMap = CacheMap(cacheItem.id, cacheItem.data.value.toMap)
  }

  def save[T](cacheId: String, key: String, data: T)(implicit writes: Writes[T]): Future[CacheMap] =
    put(cacheId)(DataKey(key), data)
      .map(_.asCacheMap)
      .recoverWith {
        case e =>
          logger.error(e.getMessage, e)
          Future.failed(e)
      }

  def get[T](cacheId: String, key: String)(implicit reads: Reads[T]): Future[Option[T]] =
    get[T](cacheId)(DataKey(key))

  def findEntity[T](cacheId: String): Future[Option[CacheMap]] =
    findById(cacheId)
      .map(_.map(_.asCacheMap))

  def remove(cacheId: String, key: String): Future[Boolean] =
    delete(cacheId)(DataKey(key))
      .map(_ => true)

  def removeEntity(cacheId: String): Future[Boolean] =
    deleteEntity(cacheId)
      .map(_ => true)

}
