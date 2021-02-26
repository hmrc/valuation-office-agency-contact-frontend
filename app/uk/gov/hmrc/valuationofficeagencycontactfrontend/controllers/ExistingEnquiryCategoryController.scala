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

import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.ExistingEnquiryCategoryForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{BusinessRatesSubcategoryId, CouncilTaxSubcategoryId, EnquiryCategoryId, ExistingEnquiryCategoryId, HousingAllowanceSubcategoryId, OtherSubcategoryId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.existingEnquiryCategory
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ExistingEnquiryCategoryController @Inject()(
                                                   appConfig: FrontendAppConfig,
                                                   override val messagesApi: MessagesApi,
                                                   dataCacheConnector: DataCacheConnector,
                                                   navigator: Navigator,
                                                   getData: DataRetrievalAction,
                                                   requireData: DataRequiredAction,
                                                   existingEnquiryCategory: existingEnquiryCategory,
                                                   cc: MessagesControllerComponents
                                                 ) extends FrontendController(cc) with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode) = (getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.existingEnquiryCategory match {
      case None => ExistingEnquiryCategoryForm()
      case Some(value) => ExistingEnquiryCategoryForm().fill(value)
    }
    enquiryBackLink(request.userAnswers) match {
      case Right(link) => Ok(existingEnquiryCategory(appConfig, preparedForm, mode, link))
      case Left(msg) => {
        Logger.warn(s"Navigation for Existing Enquiry Category page reached with error $msg")
        throw new RuntimeException(s"Navigation for Existing Enquiry Category page reached with error $msg")
      }
    }
  }

  def onSubmit(mode: Mode) = getData.async {
    implicit request =>
      ExistingEnquiryCategoryForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(existingEnquiryCategory(appConfig, formWithErrors, mode, ""))),
        value => {
          for {
            _ <- saveSubCategoryInCache(value, request.sessionId)
            _ <- dataCacheConnector.remove(request.sessionId, EnquiryCategoryId.toString)
            cacheMap <- dataCacheConnector.save[String](request.sessionId, ExistingEnquiryCategoryId.toString, value)
          } yield Redirect(navigator.nextPage(ExistingEnquiryCategoryId, mode)(new UserAnswers(cacheMap)))
        }
      )
  }

  def redirect: Action[AnyContent] = Action {
    Redirect(routes.EnquiryCategoryController.onPageLoad(NormalMode))
  }

  private def saveSubCategoryInCache(subcategory: String, sessionId: String) = {
    subcategory match {
      case "council_tax"       => dataCacheConnector.save[String](sessionId, CouncilTaxSubcategoryId.toString, subcategory)
      case "business_rates"    => dataCacheConnector.save[String](sessionId, BusinessRatesSubcategoryId.toString, subcategory)
      case "housing_allowance" => dataCacheConnector.save[String](sessionId, HousingAllowanceSubcategoryId.toString, subcategory)
      case "other"             => dataCacheConnector.save[String](sessionId, OtherSubcategoryId.toString, subcategory)
      case _                   => Future.unit
    }
  }

  private[controllers] def enquiryBackLink(answers: UserAnswers): Either[String, String] = {
    answers.contactReason match {
      case Some("new_enquiry") => Right(routes.ContactReasonController.onPageLoad().url)
      case Some("more_details") => Right(routes.ContactReasonController.onPageLoad().url)
      case Some("update_existing") => Right(routes.EnquiryDateController.onPageLoad().url)
      case _ => Left(s"Unknown enquiry category in enquiry key")
    }
  }
}
