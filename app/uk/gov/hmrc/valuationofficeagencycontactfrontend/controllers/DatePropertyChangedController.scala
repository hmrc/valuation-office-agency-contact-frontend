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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.DatePropertyChangedForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{ContactDetailsId, DatePropertyChangedId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.datePropertyChanged
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DatePropertyChangedController @Inject()(val appConfig: FrontendAppConfig,
                                              override val messagesApi: MessagesApi,
                                              dataCacheConnector: DataCacheConnector,
                                              navigator: Navigator,
                                              getData: DataRetrievalAction,
                                              requireData: DataRequiredAction,
                                              datePropertyChanged: datePropertyChanged,
                                              cc: MessagesControllerComponents
                                             ) extends FrontendController(cc) with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.datePropertyChanged match {
      case None => DatePropertyChangedForm()
      case Some(value) => DatePropertyChangedForm().fill(Some(value))
    }

    Ok(datePropertyChanged(appConfig, preparedForm, mode, getEnquiryKey(request.userAnswers), backLink(request.userAnswers)))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      DatePropertyChangedForm().bindFromRequest().fold(
        (formWithErrors: Form[Option[LocalDate]]) =>
          Future.successful(BadRequest(datePropertyChanged(appConfig, formWithErrors, mode, getEnquiryKey(request.userAnswers), backLink(request.userAnswers)))),
        value =>
          for {
            _ <- dataCacheConnector.remove(request.sessionId, DatePropertyChangedId.toString)
            cacheMap <- if (value.nonEmpty) {
              dataCacheConnector.save[LocalDate](request.sessionId, DatePropertyChangedId.toString, value.get)
            }
            else {
              dataCacheConnector.save[String](request.sessionId, "", "")
            }
          } yield Redirect(navigator.nextPage(DatePropertyChangedId, mode)(new UserAnswers(cacheMap)))
      )
  }

  private def getEnquiryKey(answers: UserAnswers): String = {
    enquiryKey(answers).getOrElse {
      Logger.warn(s"Navigation for Date Property Changed page reached with error - Unknown enquiry category in enquiry key")
      throw new RuntimeException(s"Navigation for  Date Property Changed page reached with error Unknown enquiry category in enquiry key")
    }
  }

  private[controllers] def enquiryKey(answers: UserAnswers): Either[String, String] = {
    answers.councilTaxSubcategory match {
      case Some("council_tax_property_poor_repair") => Right("datePropertyChanged.poorRepair")
      case Some("council_tax_business_uses") => Right("datePropertyChanged.business")
      case _ => Left("Unknown enquiry category in enquiry key")
    }
  }

  private def backLink(answers: UserAnswers) = {
    answers.councilTaxSubcategory match {
      case Some("council_tax_property_poor_repair") => routes.PropertyWindWaterController.onEnquiryLoad().url
      case Some("council_tax_business_uses") => routes.CouncilTaxBusinessController.onPageLoad().url
      case _ => routes.PropertyWindWaterController.onEnquiryLoad().url
    }
  }
}