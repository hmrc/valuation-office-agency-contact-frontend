/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.{AuditingService, DataCacheConnector}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.CouncilTaxAnnexeSelfContainedEnquiryId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{AnnexeCookingWashingForm, AnnexeForm, AnnexeSelfContainedForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{CouncilTaxAnnexeEnquiryId, CouncilTaxAnnexeHaveCookingId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxAnnexe => council_tax_annexe}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeSelfContainedEnquiry => annexe_self_contained_enquiry}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeNotSelfContained => annexe_not_self_contained}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeNoFacilities => annexe_no_facilities}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeSelfContained => annexe_self_contained}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeCookingWashingEnquiry => annexe_cooking_washing_enquiry}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeRemoved => annexe_removed}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CouncilTaxAnnexeController @Inject()(val appConfig: FrontendAppConfig,
                                           override val messagesApi: MessagesApi,
                                           auditService: AuditingService,
                                           dataCacheConnector: DataCacheConnector,
                                           navigator: Navigator,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           councilTaxAnnexe: council_tax_annexe,
                                           annexeSelfContainedEnquiry: annexe_self_contained_enquiry,
                                           annexeNotSelfContained: annexe_not_self_contained,
                                           annexeNoFacilities: annexe_no_facilities,
                                           annexeSelfContained: annexe_self_contained,
                                           annexeCookingWashingEnquiry: annexe_cooking_washing_enquiry,
                                           annexeRemoved: annexe_removed,
                                           cc: MessagesControllerComponents
                                         ) extends FrontendController(cc) with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.annexeEnquiry match {
        case None => AnnexeForm()
        case Some(value) => AnnexeForm().fill(value)
      }
      Ok(councilTaxAnnexe(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = getData.async {
    implicit request =>
      AnnexeForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(councilTaxAnnexe(appConfig, formWithErrors, mode))),
        value =>
          for {
            _ <- dataCacheConnector.remove(request.sessionId, CouncilTaxAnnexeEnquiryId.toString)
            cacheMap <- dataCacheConnector.save[String](request.sessionId, CouncilTaxAnnexeEnquiryId.toString, value)
          } yield {
            auditService.sendRadioButtonSelection(request.uri, "annexe" -> value)
            Redirect(navigator.nextPage(CouncilTaxAnnexeEnquiryId, mode).apply(new UserAnswers(cacheMap)))
          }
      )
  }

  def onRemovedPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(annexeRemoved(appConfig))
  }

  def onSelfContainedEnquiryPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.annexeSelfContainedEnquiry match {
        case None => AnnexeSelfContainedForm()
        case Some(value) => AnnexeSelfContainedForm().fill(value)
      }
      Ok(annexeSelfContainedEnquiry(appConfig, preparedForm))
  }

  def onSelfContainedSubmit: Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      AnnexeSelfContainedForm().bindFromRequest.fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(annexeSelfContainedEnquiry(appConfig, formWithErrors))),
        value => {
          auditService.sendRadioButtonSelection(request.uri, "annexeSelfContainedEnquiry" -> value)
          dataCacheConnector.save[String](request.sessionId, CouncilTaxAnnexeSelfContainedEnquiryId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(CouncilTaxAnnexeSelfContainedEnquiryId, NormalMode).apply(new UserAnswers(cacheMap))))
        }
      )
  }

  def onNotSelfContainedPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(annexeNotSelfContained(appConfig))
  }

  def onFacilitiesPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(annexeNoFacilities(appConfig))
  }

  def onSelfContainedPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      Ok(annexeSelfContained(appConfig))
  }

  def onHaveCookingWashingPageLoad:Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.annexeHaveCookingWashing match {
        case None => AnnexeCookingWashingForm()
        case Some(value) => AnnexeCookingWashingForm().fill(value)
      }
      Ok(annexeCookingWashingEnquiry(appConfig, preparedForm))
  }

  def onHaveCookingWashingSubmit: Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      AnnexeCookingWashingForm().bindFromRequest.fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(annexeCookingWashingEnquiry(appConfig, formWithErrors))),
        value => {
          auditService.sendRadioButtonSelection(request.uri, "annexeCookingWashing" -> value)
          dataCacheConnector.save[String](request.sessionId, CouncilTaxAnnexeHaveCookingId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(CouncilTaxAnnexeHaveCookingId, NormalMode).apply(new UserAnswers(cacheMap))))
        }
      )
  }
}
