/*
 * Copyright 2017 HM Revenue & Customs
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
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.TellUsMoreForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.TellUsMoreId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, TellUsMore}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.tellUsMore

import scala.concurrent.Future

class TellUsMoreController @Inject()(appConfig: FrontendAppConfig,
                                     override val messagesApi: MessagesApi,
                                     dataCacheConnector: DataCacheConnector,
                                     navigator: Navigator,
                                     getData: DataRetrievalAction,
                                     requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def councilTaxKey(answers: UserAnswers): Either[String, String] = {

    answers.councilTaxSubcategory match {
      case Some("council_tax_band") => Right("councilTaxSubcategory.council_tax_band")
      case Some("council_tax_calculated") => Right("councilTaxSubcategory.council_tax_calculated")
      case Some("council_tax_changes") => Right("councilTaxSubcategory.council_tax_changes")
      case Some("council_tax_challenge") => Right("councilTaxSubcategory.council_tax_challenge")
      case Some("council_tax_other") => Right("councilTaxSubcategory.council_tax_other")
      case Some(sel) => Left(sel)
      case None => Left("Returned None from council tax subcategory")
    }
  }

  def businessRatesKey(answers: UserAnswers): Either[String, String] = {

    answers.businessRatesSubcategory match {
      case Some("business_rates_rateable_value") => Right("businessRatesSubcategory.business_rates_rateable_value")
      case Some("business_rates_business_rates") => Right("businessRatesSubcategory.business_rates_business_rates")
      case Some("business_rates_update_details") => Right("businessRatesSubcategory.business_rates_update_details")
      case Some("business_rates_challenge") => Right("businessRatesSubcategory.business_rates_challenge")
      case Some("business_rates_other") => Right("businessRatesSubcategory.business_rates_other")
      case Some(sel) => Left(sel)
      case None => Left("Returned None from business rates subcategory")
    }
  }

  def enquiryKey(answers: UserAnswers): Either[String, String] = {

    answers.enquiryCategory match {
      case Some("council_tax") => councilTaxKey(answers)
      case Some("business_rates") => businessRatesKey(answers)
      case _ => Left("Unknown enquiry category in enquiry key")
    }
  }

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.tellUsMore match {
        case None => TellUsMoreForm()
        case Some(value) => TellUsMoreForm().fill(value)
      }

      enquiryKey(request.userAnswers) match {
        case Right(key) => Ok(tellUsMore(appConfig, preparedForm, mode, key))
        case Left(msg) => {
          Logger.warn(s"Navigation for Tell us more page reached with error $msg")
          throw new RuntimeException(s"Navigation for Tell us more page reached with error $msg")
        }
      }

  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      TellUsMoreForm().bindFromRequest().fold(
        (formWithErrors: Form[TellUsMore]) =>
          Future.successful(BadRequest(tellUsMore(appConfig, formWithErrors, mode, ""))),
        (value) =>
          dataCacheConnector.save[TellUsMore](request.sessionId, TellUsMoreId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(TellUsMoreId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
