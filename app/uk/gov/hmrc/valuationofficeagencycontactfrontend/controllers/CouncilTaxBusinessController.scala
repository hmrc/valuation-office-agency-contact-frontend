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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.CouncilTaxBusinessEnquiryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.CouncilTaxBusinessEnquiryId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxBusinessEnquiry => council_tax_business_enquiry}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertySmallPartUsed => small_part_used}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{businessRatesNoNeedToPay => business_rates_no_need_to_pay}

import javax.inject.Inject
import scala.concurrent.Future

class CouncilTaxBusinessController @Inject()(appConfig: FrontendAppConfig,
                                             override val messagesApi: MessagesApi,
                                             auditService: AuditingService,
                                             dataCacheConnector: DataCacheConnector,
                                             navigator: Navigator,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             councilTaxBusinessEnquiry: council_tax_business_enquiry,
                                             propertySmallPartUsed: small_part_used,
                                             businessRatesNoNeedToPay: business_rates_no_need_to_pay,
                                             cc: MessagesControllerComponents
                                            ) extends FrontendController(cc) with I18nSupport {

  implicit val ec = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.councilTaxBusinessEnquiry match {
        case None => CouncilTaxBusinessEnquiryForm()
        case Some(value) => CouncilTaxBusinessEnquiryForm().fill(value)
      }

      Ok(councilTaxBusinessEnquiry(appConfig, preparedForm, mode, backLink(request.userAnswers, mode)))
  }

  def onEnquirySubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData) async {
    implicit request =>
      CouncilTaxBusinessEnquiryForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(councilTaxBusinessEnquiry(appConfig, formWithErrors, mode, backLink(request.userAnswers, mode)))),
        value =>
          for {
            cacheMap <- dataCacheConnector.save[String](request.sessionId, CouncilTaxBusinessEnquiryId.toString, value)
          } yield {
            auditService.sendRadioButtonSelection(request.uri, "councilTaxBusinessEnquiry" -> value)
            Redirect(navigator.nextPage(CouncilTaxBusinessEnquiryId, mode).apply(new UserAnswers(cacheMap)))
          }
      )
  }

  def onSmallPartUsedPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(propertySmallPartUsed(appConfig))
  }

  def onSmallPartUsedBusinessRatesPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(businessRatesNoNeedToPay(appConfig))
  }

  private def backLink(answers: UserAnswers, mode: Mode): String = {
    answers.businessRatesSubcategory match {
      case Some("business_rates_from_home") => routes.BusinessRatesSubcategoryController.onPageLoad(mode).url
      case _ => routes.CouncilTaxSubcategoryController.onPageLoad(mode).url
    }
  }
}
