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

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.vo.contact.frontend.Navigator
import uk.gov.hmrc.vo.contact.frontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.vo.contact.frontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.vo.contact.frontend.forms.{AnnexeCookingWashingForm, AnnexeForm, AnnexeSelfContainedForm}
import uk.gov.hmrc.vo.contact.frontend.identifiers.{CouncilTaxAnnexeEnquiryId, CouncilTaxAnnexeHaveCookingId, CouncilTaxAnnexeSelfContainedEnquiryId}
import uk.gov.hmrc.vo.contact.frontend.models.{Mode, NormalMode}
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers
import uk.gov.hmrc.vo.contact.frontend.views.html.*

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CouncilTaxAnnexeController @Inject() (
  override val messagesApi: MessagesApi,
  auditService: AuditingService,
  dataCacheConnector: DataCacheConnector,
  navigator: Navigator,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  councilTaxAnnexeView: councilTaxAnnexe,
  annexeSelfContainedEnquiryView: annexeSelfContainedEnquiry,
  annexeNotSelfContainedView: annexeNotSelfContained,
  annexeNoFacilitiesView: annexeNoFacilities,
  annexeSelfContainedView: annexeSelfContained,
  annexeCookingWashingEnquiryView: annexeCookingWashingEnquiry,
  annexeRemovedView: annexeRemoved,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
  with I18nSupport:

  given ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData `andThen` requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.annexeEnquiry match
        case None        => AnnexeForm()
        case Some(value) => AnnexeForm().fill(value)
      Ok(councilTaxAnnexeView(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = getData.async { implicit request =>
    AnnexeForm().bindFromRequest().fold(
      formWithErrors => BadRequest(councilTaxAnnexeView(formWithErrors, mode)),
      value =>
        for
          _        <- dataCacheConnector.remove(request.sessionId, CouncilTaxAnnexeEnquiryId.toString)
          cacheMap <- dataCacheConnector.save[String](request.sessionId, CouncilTaxAnnexeEnquiryId.toString, value)
        yield
          auditService.sendRadioButtonSelection(request.uri, "annexe" -> value)
          Redirect(navigator.nextPage(CouncilTaxAnnexeEnquiryId, mode).apply(UserAnswers(cacheMap)))
    )
  }

  def onRemovedPageLoad: Action[AnyContent] = (getData `andThen` requireData) { implicit request =>
    Ok(annexeRemovedView())
  }

  def onSelfContainedEnquiryPageLoad: Action[AnyContent] = (getData `andThen` requireData) { implicit request =>
    val preparedForm = request.userAnswers.annexeSelfContainedEnquiry match
      case None        => AnnexeSelfContainedForm()
      case Some(value) => AnnexeSelfContainedForm().fill(value)
    Ok(annexeSelfContainedEnquiryView(preparedForm))
  }

  def onSelfContainedSubmit: Action[AnyContent] = (getData `andThen` requireData).async { implicit request =>
    AnnexeSelfContainedForm().bindFromRequest().fold(
      formWithErrors => BadRequest(annexeSelfContainedEnquiryView(formWithErrors)),
      value =>
        auditService.sendRadioButtonSelection(request.uri, "annexeSelfContainedEnquiry" -> value)
        dataCacheConnector.save[String](request.sessionId, CouncilTaxAnnexeSelfContainedEnquiryId.toString, value).map(cacheMap =>
          Redirect(navigator.nextPage(CouncilTaxAnnexeSelfContainedEnquiryId, NormalMode).apply(UserAnswers(cacheMap)))
        )
    )
  }

  def onNotSelfContainedPageLoad: Action[AnyContent] = (getData `andThen` requireData) { implicit request =>
    Ok(annexeNotSelfContainedView())
  }

  def onFacilitiesPageLoad: Action[AnyContent] = (getData `andThen` requireData) { implicit request =>
    Ok(annexeNoFacilitiesView())
  }

  def onSelfContainedPageLoad: Action[AnyContent] = (getData `andThen` requireData) { implicit request =>
    Ok(annexeSelfContainedView())
  }

  def onHaveCookingWashingPageLoad: Action[AnyContent] = (getData `andThen` requireData) { implicit request =>
    val preparedForm = request.userAnswers.annexeHaveCookingWashing match
      case None        => AnnexeCookingWashingForm()
      case Some(value) => AnnexeCookingWashingForm().fill(value)
    Ok(annexeCookingWashingEnquiryView(preparedForm))
  }

  def onHaveCookingWashingSubmit: Action[AnyContent] = (getData `andThen` requireData).async { implicit request =>
    AnnexeCookingWashingForm().bindFromRequest().fold(
      formWithErrors => BadRequest(annexeCookingWashingEnquiryView(formWithErrors)),
      value =>
        auditService.sendRadioButtonSelection(request.uri, "annexeCookingWashing" -> value)
        dataCacheConnector.save[String](request.sessionId, CouncilTaxAnnexeHaveCookingId.toString, value).map(cacheMap =>
          Redirect(navigator.nextPage(CouncilTaxAnnexeHaveCookingId, NormalMode).apply(UserAnswers(cacheMap)))
        )
    )
  }
