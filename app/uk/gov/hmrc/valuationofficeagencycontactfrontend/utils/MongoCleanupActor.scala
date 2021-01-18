/*
 * Copyright 2021 HM Revenue & Customs
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

import akka.actor.{Actor, Props}
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.collections.bson.BSONCollection
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MongoCleanupActor.{CleanupDone, DoCleanup}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

@Singleton
class MongoCleanupActor @Inject()(mongo: ReactiveMongoComponent)(implicit val ec: ExecutionContext) extends Actor {

  this.context.system.scheduler.scheduleOnce(1 minute, self, DoCleanup)

  val collectionToDelete = "valuation-office-agency-contact-frontend"
  val log = Logger(this.getClass)

  override def receive: Receive = {
    case DoCleanup => doCleanup()
    case CleanupDone => log.info("Cleanup future successfully finished.")
  }

  def doCleanup(): Unit = {
    import akka.pattern.{ pipe }
    val collection:BSONCollection = mongo.mongoConnector.db().collection(collectionToDelete)

    val cleanupResult = collection.drop(false).recoverWith {
      case ex: Exception => {
        log.warn("Unable to delete old collection, strange error, collection.drop(false) should not fail if collection doesn't exist.", ex)
        Future.successful(false)
      }
    }.map { deleteResult =>
      if(deleteResult) {
        log.info("Old collection successfully deleted")
      } else {
        log.warn("Unable to delete collection. Maybe it doesn't exist. Check for other exceptions.")
      }
      CleanupDone
    }

    cleanupResult pipeTo sender()
  }

}

object MongoCleanupActor {
  case object DoCleanup
  case object CleanupDone

  def props(mongo: ReactiveMongoComponent)(implicit ec: ExecutionContext): Props = {
    Props(classOf[MongoCleanupActor], mongo, ec)
  }

}
