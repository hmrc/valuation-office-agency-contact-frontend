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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.TellUsMoreForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.TellUsMoreId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, NormalMode, TellUsMore}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{tellUsMore => tell_us_more}

import scala.concurrent.{ExecutionContext, Future}

class TellUsMoreController @Inject()(appConfig: FrontendAppConfig,
                                     override val messagesApi: MessagesApi,
                                     dataCacheConnector: DataCacheConnector,
                                     navigator: Navigator,
                                     getData: DataRetrievalAction,
                                     requireData: DataRequiredAction,
                                     tellUsMore: tell_us_more,
                                     cc: MessagesControllerComponents
                                    ) extends FrontendController(cc) with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val key = requiredErrorMessage(request.userAnswers)

      val preparedForm = request.userAnswers.tellUsMore match {
        case None => TellUsMoreForm(key)
        case Some(value) => TellUsMoreForm(key).fill(value)
      }

      Ok(tellUsMore(appConfig, preparedForm, mode, getEnquiryKey(request.userAnswers), backLink(request.userAnswers)))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      TellUsMoreForm(requiredErrorMessage(request.userAnswers)).bindFromRequest().fold(
        (formWithErrors: Form[TellUsMore]) =>
          Future.successful(BadRequest(tellUsMore(appConfig, formWithErrors, mode, getEnquiryKey(request.userAnswers), backLink(request.userAnswers)))),
        (value) =>
          dataCacheConnector.save[TellUsMore](request.sessionId, TellUsMoreId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(TellUsMoreId, mode)(new UserAnswers(cacheMap))))
      )
  }

  private def requiredErrorMessage(userAnswers: UserAnswers): String =
    if (userAnswers.propertyWindEnquiry.isDefined) "error.tellUsMore.poorRepair.required"
    else if(userAnswers.councilTaxSubcategory.contains("council_tax_area_change")) "error.tellUsMore.areaChanged.required"
    else "error.tell_us_more.required"

  private def getEnquiryKey(answers: UserAnswers): String = {
    enquiryKey(answers).getOrElse {
      Logger.warn(s"Navigation for Tell us more page reached with error - Unknown enquiry category in enquiry key")
      throw new RuntimeException(s"Navigation for Tell us more page reached with error Unknown enquiry category in enquiry key")
    }
  }

  private[controllers] def enquiryKey(answers: UserAnswers): Either[String, String] = {
    (answers.enquiryCategory, answers.councilTaxSubcategory) match {
      case (Some("council_tax"), Some("council_tax_property_poor_repair")) => Right("tellUsMore.poorRepair")
      case (Some("council_tax"), Some("council_tax_business_uses")) => Right("tellUsMore.business")
      case (Some("council_tax"), Some("council_tax_area_change")) => Right("tellUsMore.areaChange")
      case (Some("council_tax"), _) => Right("tellUsMore.ct-reference")
      case (Some("business_rates"), _) => Right("tellUsMore.ndr-reference")
      case _ => Left("Unknown enquiry category in enquiry key")
    }
  }

  private def backLink(answers: UserAnswers) = {
    answers.councilTaxSubcategory match {
      case Some("council_tax_property_poor_repair") => routes.DatePropertyChangedController.onPageLoad().url
      case Some("council_tax_business_uses") => routes.DatePropertyChangedController.onPageLoad().url
      case Some("council_tax_area_change") => routes.DatePropertyChangedController.onPageLoad().url
      case _ => routes.PropertyAddressController.onPageLoad(NormalMode).url
    }
  }

}
