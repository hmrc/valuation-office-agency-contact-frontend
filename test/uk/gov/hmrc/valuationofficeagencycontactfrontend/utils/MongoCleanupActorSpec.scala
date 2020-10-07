/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.utils

import akka.testkit.TestProbe
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONObjectID, BSONString}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

import scala.concurrent.ExecutionContext

class MongoCleanupActorSpec extends PlaySpec with FutureAwaits with DefaultAwaitTimeout with GuiceOneAppPerSuite {

  implicit def as = app.actorSystem

  def mongo = app.injector.instanceOf[ReactiveMongoComponent]

  implicit def ec = app.injector.instanceOf[ExecutionContext]

  "MongoCleanupActor" should {
    "Drop collection in mongo" in {

      val bsonId = BSONObjectID.generate()

      val oldCollection:BSONCollection = mongo.mongoConnector.db().collection("valuation-office-agency-contact-frontend")

      await(oldCollection.insert(true, reactivemongo.api.WriteConcern.Acknowledged).one(BSONDocument(
        "_id" -> bsonId,
        "testKey" -> BSONString("value")
      )))

      val probe = TestProbe("testProbe")

      val testActor = as.actorOf(MongoCleanupActor.props(mongo))

      probe.send(testActor, MongoCleanupActor.DoCleanup)

      probe.expectMsg(MongoCleanupActor.CleanupDone)

      await(oldCollection.count(
        skip = 0,
        hint = None,
        limit = None,
        selector = None,
        readConcern = reactivemongo.api.ReadConcern.Majority)) mustBe(0)

      //await(oldCollection.count()) mustBe(0)


    }
  }






}
