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

import play.api.data.Form
import play.api.i18n.{DefaultMessagesApi, Lang, Messages, MessagesImpl}
import play.api.libs.json.JsString
import play.api.mvc.{Call, Request}
import play.api.test.Helpers.*
import uk.gov.hmrc.vo.contact.frontend.connectors.{AuditingService, FakeDataCacheConnector}
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.*
import uk.gov.hmrc.vo.contact.frontend.journey.JourneyMap
import uk.gov.hmrc.vo.contact.frontend.journey.model.NotImplemented
import uk.gov.hmrc.vo.contact.frontend.journey.pages.{HousingBenefitAllowancesRouter, HousingBenefitAppeals, HousingBenefitEnquiry}
import uk.gov.hmrc.vo.contact.frontend.models.{CacheMap, NormalMode}
import uk.gov.hmrc.vo.contact.frontend.utils.{MessageControllerComponentsHelpers, UserAnswers}
import uk.gov.hmrc.vo.contact.frontend.views.html.journey.{categoryRouter, customizedContent, notImplemented, singleTextarea}

class JourneyControllerSpec extends ControllerSpecBase:

  private val pageKey = HousingBenefitAllowancesRouter.key
  private val form    = HousingBenefitAllowancesRouter.form

  def userAnswers                                  = UserAnswers(emptyCacheMap)
  def categoryRouterTemplate: categoryRouter       = inject[categoryRouter]
  def singleTextareaTemplate: singleTextarea       = inject[singleTextarea]
  def customizedContentTemplate: customizedContent = inject[customizedContent]
  def notImplementedTemplate: notImplemented       = inject[notImplemented]

  private def journeyMap   = inject[JourneyMap]
  private def auditService = inject[AuditingService]

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    JourneyController(
      journeyMap,
      auditService,
      FakeDataCacheConnector,
      dataRetrievalAction,
      DataRequiredActionImpl(ec),
      categoryRouterTemplate,
      singleTextareaTemplate,
      customizedContentTemplate,
      notImplementedTemplate,
      MessageControllerComponentsHelpers.stubMessageControllerComponents,
      messagesApi
    )

  private def viewAsString(form: Form[String] = form): String =
    categoryRouterTemplate(form, pageKey, HousingBenefitAllowancesRouter.previousPage(userAnswers).url, HousingBenefitAllowancesRouter)(
      using getRequest,
      messages
    ).toString

  "JourneyController" should {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(pageKey)(getRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewAsString()

      journeyMap.journeyMap.values.map(_.key).foreach { key =>
        val result = controller().onPageLoad(key)(getRequest)
        status(result) shouldBe OK
      }
    }

    "return NOT_FOUND for unknown page key" in {
      val result = controller().onPageLoad("unknown-page-key")(getRequest)
      status(result) shouldBe NOT_FOUND
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData       = Map(HousingBenefitAllowancesRouter.key -> JsString(HousingBenefitAllowancesRouter.options.head))
      val getRelevantData = FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(pageKey)(getRequest)

      contentAsString(result) shouldBe viewAsString(form.fill(HousingBenefitAllowancesRouter.options.head))
    }

    "redirect to start page EnquiryCategory when valid data is submitted, but no data retrieved form cache" in {
      val postRequest = getRequest.withMethod("POST")
        .withFormUrlEncodedBody((HousingBenefitAllowancesRouter.fieldId, HousingBenefitAllowancesRouter.options.head))

      val result = controller().onSubmit(pageKey)(postRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.EnquiryCategoryController.onPageLoad(NormalMode).url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = getRequest.withFormUrlEncodedBody((HousingBenefitAllowancesRouter.fieldId, "invalid value"))
      val boundForm   = form.bind(Map(HousingBenefitAllowancesRouter.fieldId -> "invalid value"))

      val result = controller().onSubmit(pageKey)(postRequest)

      status(result)          shouldBe BAD_REQUEST
      contentAsString(result) shouldBe viewAsString(boundForm)
    }

    "return error page if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(pageKey)(getRequest)
      status(result) shouldBe SEE_OTHER
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(pageKey)(getRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = getRequest.withFormUrlEncodedBody((HousingBenefitAllowancesRouter.fieldId, HousingBenefitAllowancesRouter.options.head))
      val result      = controller(dontGetAnyData).onSubmit(pageKey)(postRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "handle NotImplemented page" in {
      object NotImplementedPage extends NotImplemented("some-not-implemented-key") {
        override def previousPage: UserAnswers => Call = _ => appStartPage
      }

      given request: Request[?] = getRequest
      given messages: Messages  = MessagesImpl(Lang("en"), new DefaultMessagesApi)

      NotImplementedPage.previousPage(userAnswers).url shouldBe NotImplementedPage.appStartPage.url

      assertThrows[NotImplementedError] {
        NotImplementedPage.nextPage(userAnswers).url
      }

      val html = notImplementedTemplate(NotImplementedPage.key, "/back/url", NotImplementedPage).toString()
      html should include(NotImplementedPage.key)
      html should include("/back/url")
    }

    "handle customized content HousingBenefitAppeals" in {
      val customizedContentPage = HousingBenefitAppeals

      given request: Request[?] = getRequest
      given messages: Messages  = MessagesImpl(Lang("en"), new DefaultMessagesApi)

      customizedContentPage.previousPage(userAnswers).url shouldBe routes.JourneyController.onPageLoad(HousingBenefitEnquiry.key).url

      assertThrows[NotImplementedError] {
        customizedContentPage.nextPage(userAnswers).url
      }

      val html = customizedContentTemplate("/back/url", customizedContentPage).toString()
      html should include("housingBenefitAppeals.label - service.name - gov.name")
      html should include("/back/url")
      html should include("https://www.gov.uk/appeal-housing-benefit-decision")
    }

  }
