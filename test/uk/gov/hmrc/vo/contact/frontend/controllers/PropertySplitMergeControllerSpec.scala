/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.contact.frontend.controllers

import play.api.test.Helpers.*
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.vo.contact.frontend.views.html.propertySplitMerge
import uk.gov.hmrc.vo.contact.frontend.views.html.propertySplitMerge as property_split_merge

import javax.inject.Singleton

@Singleton
class PropertySplitMergeControllerSpec extends ControllerSpecBase:

  def propertySplitMergeEnquiry: propertySplitMerge = app.injector.instanceOf[property_split_merge]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    PropertySplitMergeController(
      messagesApi,
      dataRetrievalAction,
      DataRequiredActionImpl(ec),
      propertySplitMergeEnquiry,
      MessageControllerComponentsHelpers.stubMessageControllerComponents
    )

  def viewAsString: String = propertySplitMergeEnquiry()(using fakeRequest, messages).toString()

  "Property Permanent Changes Controller" must {
    "return the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)
      contentAsString(result) mustBe propertySplitMergeEnquiry()(using fakeRequest, messages).toString
    }

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString
    }

  }
