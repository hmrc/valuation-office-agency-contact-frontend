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

import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.DatePropertyChangedForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.DatePropertyChangedId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{CacheMap, Mode, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.datePropertyChanged
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DatePropertyChangedController @Inject() (
  val appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  datePropertyChanged: datePropertyChanged,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport {

  private val log = Logger(this.getClass)

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.datePropertyChanged match {
      case None        => DatePropertyChangedForm()
      case Some(value) => DatePropertyChangedForm().fill(Some(value))
    }

    Ok(datePropertyChanged(appConfig, preparedForm, mode, getEnquiryKey(request.userAnswers), backLink(request.userAnswers, mode)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      DatePropertyChangedForm().bindFromRequest().fold(
        (formWithErrors: Form[Option[LocalDate]]) =>
          Future.successful(
            BadRequest(datePropertyChanged(appConfig, formWithErrors, mode, getEnquiryKey(request.userAnswers), backLink(request.userAnswers, mode)))
          ),
        value =>
          for
            _        <- dataCacheConnector.remove(request.sessionId, DatePropertyChangedId.toString)
            cacheMap <- if value.nonEmpty then dataCacheConnector.save[LocalDate](request.sessionId, DatePropertyChangedId.toString, value.get)
                        else dataCacheConnector.fetch(request.sessionId).map(_.getOrElse(new CacheMap(request.sessionId, Map.empty)))
          yield Redirect(navigator.nextPage(DatePropertyChangedId, mode).apply(new UserAnswers(cacheMap)))
      )
  }

  private def getEnquiryKey(answers: UserAnswers): String =
    enquiryKey(answers).getOrElse {
      log.warn("Navigation for Date Property Changed page reached with error - Unknown enquiry category in enquiry key")
      throw new RuntimeException("Navigation for  Date Property Changed page reached with error Unknown enquiry category in enquiry key")
    }

  private[controllers] def enquiryKey(answers: UserAnswers): Either[String, String] =
    (answers.councilTaxSubcategory, answers.businessRatesSubcategory) match {
      case (Some("council_tax_business_uses"), None) => Right("datePropertyChanged.business")
      case (Some("council_tax_area_change"), None)   => Right("datePropertyChanged.areaChange")
      case (None, Some("business_rates_from_home"))  => Right("datePropertyChanged.business")
      case (None, Some("business_rates_not_used"))   => Right("datePropertyChanged.notUsed")
      case _                                         => Left("Unknown enquiry category in enquiry key")
    }

  private def backLink(answers: UserAnswers, mode: Mode) =
    (answers.councilTaxSubcategory, answers.businessRatesSubcategory) match {
      case (Some("council_tax_business_uses"), None) => routes.CouncilTaxSubcategoryController.onPageLoad(mode).url
      case (Some("council_tax_area_change"), None)   => routes.CouncilTaxSubcategoryController.onPageLoad(mode).url
      case (None, Some("business_rates_from_home"))  => routes.BusinessRatesSubcategoryController.onPageLoad(mode).url
      case (None, Some("business_rates_not_used"))   => routes.BusinessRatesPropertyController.onPageLoad().url
      case _                                         => routes.EnquiryCategoryController.onPageLoad(NormalMode).url
    }

}
