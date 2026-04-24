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

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.vo.contact.frontend.Navigator
import uk.gov.hmrc.vo.contact.frontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.forms.FairRentEnquiryForm
import uk.gov.hmrc.vo.contact.frontend.identifiers.FairRentEnquiryId
import uk.gov.hmrc.vo.contact.frontend.models.Mode
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers
import uk.gov.hmrc.vo.contact.frontend.views.html.{checkFairRentApplication as check_fair_rent_application, fairRentEnquiry as fair_rent_enquiry, submitFairRentApplication as submit_fair_rent_application}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class FairRentEnquiryController @Inject() (
  override val messagesApi: MessagesApi,
  auditService: AuditingService,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  fairRentEnquiry: fair_rent_enquiry,
  submitFairRentApplication: submit_fair_rent_application,
  checkFairRentApplication: check_fair_rent_application,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.fairRentEnquiryEnquiry match {
        case None        => FairRentEnquiryForm()
        case Some(value) => FairRentEnquiryForm().fill(value)
      }
      Ok(fairRentEnquiry(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      FairRentEnquiryForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          BadRequest(fairRentEnquiry(formWithErrors, mode)),
        value => {
          auditService.sendRadioButtonSelection(request.uri, "fairRents" -> value)
          dataCacheConnector.save[String](request.sessionId, FairRentEnquiryId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(FairRentEnquiryId, mode).apply(UserAnswers(cacheMap)))
          )
        }
      )
  }

  def onFairRentEnquiryNew(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(submitFairRentApplication())
  }

  def onFairRentEnquiryCheck(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(checkFairRentApplication())
  }

}
