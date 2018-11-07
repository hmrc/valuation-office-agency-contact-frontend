/*
 * Copyright 2018 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.{Configuration, Logger}
import play.api.libs.json.{JsValue, Json}
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.DefaultDB
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.play.json._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
//$COVERAGE-OFF$
case class DatedCacheMap(id: String,
                         data: Map[String, JsValue],
                         lastUpdated: DateTime = DateTime.now(DateTimeZone.UTC))

object DatedCacheMap {
  implicit val dateFormat = ReactiveMongoFormats.dateTimeFormats
  implicit val formats = Json.format[DatedCacheMap]

  def apply(cacheMap: CacheMap): DatedCacheMap = DatedCacheMap(cacheMap.id, cacheMap.data)
}

class ReactiveMongoRepository(config: Configuration, mongo: () => DefaultDB)
  extends ReactiveRepository[DatedCacheMap, BSONObjectID](config.getString("appName").get, mongo, DatedCacheMap.formats) {

  val timeToLiveInSeconds: Int = config.getInt("mongodb.timeToLiveInSeconds").get

  override def indexes: Seq[Index] =  {
    Seq(
    Index(Seq("lastUpdated" -> IndexType.Ascending), name = Some("userAnswersExpiry"),
      options = BSONDocument("expireAfterSeconds" -> timeToLiveInSeconds)),
    Index(Seq("id" -> IndexType.Descending), name = Some("contact-form-id_idx")))
  }

  def upsert(cm: CacheMap): Future[Boolean] = {
    val selector = BSONDocument("id" -> cm.id)
    val cmDocument = Json.toJson(DatedCacheMap(cm))
    val modifier = BSONDocument("$set" -> cmDocument)

    collection.update(selector, modifier, upsert = true).map { lastError =>
      lastError.ok
    }
  }

  def get(id: String): Future[Option[CacheMap]] = {

    find("id" -> id)
      .map(_.headOption)
      .map(_.map(x => CacheMap(id, x.data)))

  }
}

@Singleton
class SessionRepository @Inject()(config: Configuration) {

  class DbConnection extends MongoDbConnection

  private lazy val sessionRepository = new ReactiveMongoRepository(config, new DbConnection().db)

  def apply(): ReactiveMongoRepository = sessionRepository
}
//$COVERAGE-ON$
