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

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{CouncilTaxBusinessEnquiryForm, DatePropertyChangedForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{CouncilTaxBusinessEnquiryId, DatePropertyChangedId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxBusinessEnquiry => council_tax_business_enquiry}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertySmallPartUsed => small_part_used}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{datePropertyBusinessChanged => date_property_bus_changed}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.Future

class CouncilTaxBusinessController @Inject()(appConfig: FrontendAppConfig,
                                             override val messagesApi: MessagesApi,
                                             dataCacheConnector: DataCacheConnector,
                                             navigator: Navigator,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             councilTaxBusinessEnquiry: council_tax_business_enquiry,
                                             propertySmallPartUsed: small_part_used,
                                             datePropertyBusinessChanged: date_property_bus_changed,
                                             cc: MessagesControllerComponents
                                            ) extends FrontendController(cc) with I18nSupport {

  implicit val ec = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.councilTaxBusinessEnquiry match {
        case None => CouncilTaxBusinessEnquiryForm()
        case Some(value) => CouncilTaxBusinessEnquiryForm().fill(value)
      }

      Ok(councilTaxBusinessEnquiry(appConfig, preparedForm, mode))
  }

  def onEnquirySubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData) async {
    implicit request =>
      CouncilTaxBusinessEnquiryForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(councilTaxBusinessEnquiry(appConfig, formWithErrors, mode))),
        value =>
          for {
            cacheMap <- dataCacheConnector.save[String](request.sessionId, CouncilTaxBusinessEnquiryId.toString, value)
          } yield Redirect(navigator.nextPage(CouncilTaxBusinessEnquiryId, mode)(new UserAnswers(cacheMap)))
      )
  }

  def onSmallPartUsedPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(propertySmallPartUsed(appConfig))
  }

  def onDateChangedPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.datePropertyChanged match {
      case None => DatePropertyChangedForm()
      case Some(value) => DatePropertyChangedForm().fill(Some(value))
    }

    Ok(datePropertyBusinessChanged(appConfig, preparedForm, mode))
  }

  def onDateChangedSubmit(mode: Mode): Action[AnyContent] = getData.async {
    implicit request =>
      DatePropertyChangedForm().bindFromRequest().fold(
        (formWithErrors: Form[Option[LocalDate]]) =>
          Future.successful(BadRequest(datePropertyBusinessChanged(appConfig, formWithErrors, mode))),
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
}
