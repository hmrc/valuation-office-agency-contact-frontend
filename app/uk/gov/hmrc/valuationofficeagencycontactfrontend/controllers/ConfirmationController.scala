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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{EmailConnector, LightweightContactEventsConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.ConfirmationController._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.CheckYourAnswersId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{confirmation => Confirmation}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.SatisfactionSurveyForm

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import play.api.mvc
import play.api.mvc.AnyContent

@Singleton()
class ConfirmationController @Inject() (
  val appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  val connector: LightweightContactEventsConnector,
  emailConnector: EmailConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  confirmation: Confirmation,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport {

  private val log = Logger(this.getClass)

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoadSendEmail: mvc.Action[AnyContent] = (getData andThen requireData).async { implicit request =>
    val contact = request.userAnswers.contact() match {
      case Right(ct) => ct
      case Left(msg) =>
        log.warn(s"On Sending Email - Navigation for Confirmation page reached without a contact and error $msg")
        throw new RuntimeException(s"On Sending Email - Navigation for Confirmation page reached without a contact and error $msg")
    }

    val result = connector.send(contact, messagesApi, request.userAnswers)

    result map {
      case Success(_)  =>
        enquiryKey(request.userAnswers) match {
          case Right(_)  =>
            emailConnector.sendEnquiryConfirmation(contact)
            Redirect(navigator.nextPage(CheckYourAnswersId, NormalMode).apply(request.userAnswers))
          case Left(msg) =>
            log.warn(s"Obtaining enquiry value - Navigation for Confirmation page reached with error $msg")
            throw new RuntimeException(s"Obtaining enquiry value - Navigation for Confirmation page reached with error $msg")
        }
      case Failure(ex) => throw new RuntimeException(s"Navigation for Confirmation page reached with error - ${ex.getMessage}")
    }
  }

  def onPageLoad: mvc.Action[AnyContent] = (getData andThen requireData) { implicit request =>
    val (contact, answerSections) = (request.userAnswers.contact(), request.userAnswers.answerSection) match {
      case (Right(ct), Some(as)) => (ct, as)
      case (Left(msg), _)        =>
        log.warn(s"On Page load - Navigation for Confirmation page reached without a contact and error $msg")
        throw new RuntimeException(s"On Page load - Navigation for Confirmation page reached without a contact and error $msg")
    }

    Ok(confirmation(appConfig, contact, answerSections, whatHappensNextMessages(request.userAnswers), SatisfactionSurveyForm.apply()))

  }
}

object ConfirmationController {

  private[controllers] def enquiryKey(answers: UserAnswers): Either[String, String] =
    (answers.enquiryCategory, answers.existingEnquiryCategory) match {
      case (Some("council_tax"), _)     => Right("councilTaxSubcategory")
      case (Some("business_rates"), _)  => Right("businessRatesSubcategory")
      case (Some("housing_benefit"), _) => Right("housingBenefitSubcategory")
      case (Some("fair_rent"), _)       => Right("fairRents")
      case (_, Some("council_tax"))     => Right("councilTaxSubcategory")
      case (_, Some("business_rates"))  => Right("businessRatesSubcategory")
      case (_, Some("housing_benefit")) => Right("housingBenefitSubcategory")
      case (_, Some("fair_rent"))       => Right("fairRents")
      case (_, Some("other"))           => Right("other")
      case _                            => Left("Unknown enquiry category in enquiry key")
    }

  private[controllers] def whatHappensNextMessages(answers: UserAnswers): Seq[String] =
    (answers.enquiryCategory.isDefined, answers.existingEnquiryCategory.isDefined) match {
      case (true, false) => Seq("confirmation.new.p1")
      case (false, true) => Seq("confirmation.existing.p1", "confirmation.existing.p2")
      case _             => Seq.empty[String]
    }
}
