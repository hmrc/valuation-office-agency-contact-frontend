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

import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.vo.contact.frontend.Navigator
import uk.gov.hmrc.vo.contact.frontend.connectors.{EmailConnector, LightweightContactEventsConnector}
import uk.gov.hmrc.vo.contact.frontend.controllers.ConfirmationController.*
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.forms.SatisfactionSurveyForm
import uk.gov.hmrc.vo.contact.frontend.identifiers.CheckYourAnswersId
import uk.gov.hmrc.vo.contact.frontend.models.NormalMode
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers
import uk.gov.hmrc.vo.contact.frontend.views.html.confirmation

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

@Singleton()
class ConfirmationController @Inject() (
  override val messagesApi: MessagesApi,
  val connector: LightweightContactEventsConnector,
  emailConnector: EmailConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  confirmationView: confirmation,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport
  with Logging:

  given ExecutionContext = cc.executionContext

  def onPageLoadSendEmail: Action[AnyContent] = (getData andThen requireData).async { implicit request =>
    val contact = request.userAnswers.contact() match
      case Right(ct) => ct
      case Left(msg) =>
        logger.warn(s"On Sending Email - Navigation for Confirmation page reached without a contact and error $msg")
        throw RuntimeException(s"On Sending Email - Navigation for Confirmation page reached without a contact and error $msg")

    val result = connector.send(contact, messagesApi, request.userAnswers)

    result map {
      case Success(_)  =>
        enquiryKey(request.userAnswers) match
          case Right(_)  =>
            emailConnector.sendEnquiryConfirmation(contact)
            Redirect(navigator.nextPage(CheckYourAnswersId, NormalMode).apply(request.userAnswers))
          case Left(msg) =>
            logger.warn(s"Obtaining enquiry value - Navigation for Confirmation page reached with error $msg")
            throw RuntimeException(s"Obtaining enquiry value - Navigation for Confirmation page reached with error $msg")
      case Failure(ex) => throw RuntimeException(s"Navigation for Confirmation page reached with error - ${ex.getMessage}")
    }
  }

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) { implicit request =>
    val (contact, answerSections) = (request.userAnswers.contact(), request.userAnswers.answerSection) match
      case (Right(ct), Some(as)) => (ct, as)
      case (msg, _)              =>
        logger.warn(s"On Page load - Navigation for Confirmation page reached without a contact and error $msg")
        throw RuntimeException(s"On Page load - Navigation for Confirmation page reached without a contact and error $msg")

    Ok(confirmationView(contact, answerSections, whatHappensNextMessages(request.userAnswers), SatisfactionSurveyForm.apply()))
  }

object ConfirmationController:

  def enquiryKey(answers: UserAnswers): Either[String, String] =
    (answers.enquiryCategory, answers.existingEnquiryCategory) match
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

  def whatHappensNextMessages(answers: UserAnswers): Seq[String] =
    (answers.enquiryCategory.isDefined, answers.existingEnquiryCategory.isDefined) match
      case (true, false) => Seq("confirmation.new.p1")
      case (false, true) => Seq("confirmation.existing.p1", "confirmation.existing.p2")
      case _             => Seq.empty[String]
