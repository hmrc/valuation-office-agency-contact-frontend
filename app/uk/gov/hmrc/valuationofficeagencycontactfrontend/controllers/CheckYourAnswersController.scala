/*
 * Copyright 2018 HM Revenue & Customs
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

import com.google.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{CheckYourAnswersHelper, UserAnswers}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerSection
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{check_your_answers, enquiryCategory}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.EnquiryCategoryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.CheckYourAnswersId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, NormalMode}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.Future
import scala.util.Try

class CheckYourAnswersController @Inject()(appConfig: FrontendAppConfig,
                                           override val messagesApi: MessagesApi,
                                           navigator: Navigator,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def sections (answers: UserAnswers): Option[Seq[AnswerSection]] = {

    val checkYourAnswersHelper = new CheckYourAnswersHelper(answers)

    answers.enquiryCategory match{
      case Some("business_rates") => Some(Seq(AnswerSection(None, Seq(checkYourAnswersHelper.enquiryCategory, checkYourAnswersHelper.businessRatesSubcategory, checkYourAnswersHelper.contactDetails, checkYourAnswersHelper.propertyAddress,
        checkYourAnswersHelper.tellUsMore).flatten)))
      case Some("council_tax") => Some(Seq(AnswerSection(None, Seq(checkYourAnswersHelper.enquiryCategory, checkYourAnswersHelper.councilTaxSubcategory,checkYourAnswersHelper.contactDetails, checkYourAnswersHelper.propertyAddress,
        checkYourAnswersHelper.tellUsMore).flatten)))
      case _ => None
    }
  }

  def onPageLoad() = (getData andThen requireData) {
    implicit request =>

      sections(request.userAnswers) match {
        case Some(s) => Ok(check_your_answers(appConfig, s))
        case None => {
          Logger.warn("Navigation for Check your answers page reached without selection of enquiry by controller")
          throw new RuntimeException("Navigation for check your anwsers page reached without selection of enquiry by controller")
        }
      }
  }

  def goToConfirmationPage() = (getData andThen requireData) { implicit request =>
    Redirect(navigator.nextPage(CheckYourAnswersId, NormalMode)(request.userAnswers))
  }
}
