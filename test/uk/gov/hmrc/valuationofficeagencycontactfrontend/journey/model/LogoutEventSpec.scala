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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model

import org.scalatest._
import flatspec._
import matchers._
import play.api.libs.json.{JsObject, JsString, Json}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{ContactDetailsId, RefNumberId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{CacheMap, ContactDetails}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

/**
 * @author Yuriy Tumakha
 */
class LogoutEventSpec extends AnyFlatSpec with should.Matchers {

  "LogoutEvent" should "accept userAnswers in constructor and should be serialized to correct json" in {
    val contact     = ContactDetails("Full name", "user@email.com", "07711223344")
    val userAnswers = new UserAnswers(new CacheMap(
      "sessionId",
      Map(
        RefNumberId.toString      -> JsString("refNumber1"),
        ContactDetailsId.toString -> Json.toJson(contact)
      )
    ))
    LogoutEvent(Some(userAnswers)) shouldBe LogoutEvent(Some("refNumber1"), Some(contact))
  }

  it should "be empty when userAnswers are empty" in {
    LogoutEvent(None) shouldBe LogoutEvent(None, None)
  }

  it should "be serialized to empty json object when userAnswers are empty" in {
    Json.toJson(LogoutEvent(None)) shouldBe JsObject.empty
  }

}
