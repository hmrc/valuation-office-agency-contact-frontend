/*
 * Copyright 2019 HM Revenue & Customs
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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ContactDetailsForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.ContactDetailsId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{ContactDetails, Mode, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.contactDetails

import scala.concurrent.Future

class ContactDetailsController @Inject()(appConfig: FrontendAppConfig,
                                         override val messagesApi: MessagesApi,
                                         dataCacheConnector: DataCacheConnector,
                                         navigator: Navigator,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def enquiryBackLink(answers: UserAnswers): Either[String, String] = {
    answers.enquiryCategory match {
      case Some("council_tax") => Right(uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url)
      case Some("business_rates") => Right(uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url)
      case _ => Left("Unknown enquiry category in enquiry key")
    }
  }

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.contactDetails match {
        case None => ContactDetailsForm()
        case Some(value) => ContactDetailsForm().fill(value)
      }
      enquiryBackLink(request.userAnswers) match {
        case Right(link) => Ok(contactDetails(appConfig, preparedForm, mode, link))
        case Left(msg) => {
          Logger.warn(s"Navigation for Contact Details page reached with error $msg")
          throw new RuntimeException(s"Navigation for Contact Details page reached with error $msg")
        }
      }

  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      ContactDetailsForm().bindFromRequest().fold(
        (formWithErrors: Form[ContactDetails]) =>
          Future.successful(enquiryBackLink(request.userAnswers) match {
            case Right(link) => BadRequest(contactDetails(appConfig, formWithErrors, mode, link))
            case Left(msg) => {
              Logger.warn(s"Navigation for Contact Details page reached with error $msg")
              throw new RuntimeException(s"Navigation for Contact Details page reached with error $msg")
            }
          }),
        (value) =>
          dataCacheConnector.save[ContactDetails](request.sessionId, ContactDetailsId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(ContactDetailsId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
