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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.{AnnexeForm, AnnexeSelfContainedForm}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{CouncilTaxAnnexId, CouncilTaxAnnexeSelfContainedEnquiryId}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxAnnex => council_tax_annex}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeSelfContainedEnquiry => annexe_self_contained_enquiry}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeNotSelfContained => annexe_not_self_contained}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeNoFacilities => annexe_no_facilities}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{annexeSelfContained => annexe_self_contained}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CouncilTaxAnnexeController @Inject()(val appConfig: FrontendAppConfig,
                                           override val messagesApi: MessagesApi,
                                           dataCacheConnector: DataCacheConnector,
                                           navigator: Navigator,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           councilTaxAnnex: council_tax_annex,
                                           annexeSelfContainedEnquiry: annexe_self_contained_enquiry,
                                           annexeNotSelfContained: annexe_not_self_contained,
                                           annexeNoFacilities: annexe_no_facilities,
                                           annexeSelfContained: annexe_self_contained,
                                           cc: MessagesControllerComponents
                                         ) extends FrontendController(cc) with I18nSupport {

  implicit val ec: ExecutionContext = cc.executionContext

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.annexeEnquiry match {
        case None => AnnexeForm()
        case Some(value) => AnnexeForm().fill(value)
      }
      Ok(councilTaxAnnex(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = getData.async {
    implicit request =>
      AnnexeForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(councilTaxAnnex(appConfig, formWithErrors, mode))),
        value =>
          for {
            _ <- dataCacheConnector.remove(request.sessionId, CouncilTaxAnnexId.toString)
            cacheMap <- dataCacheConnector.save[String](request.sessionId, CouncilTaxAnnexId.toString, value)
          } yield Redirect(navigator.nextPage(CouncilTaxAnnexId, mode)(new UserAnswers(cacheMap)))
      )
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
        value =>
          dataCacheConnector.save[String](request.sessionId, CouncilTaxAnnexeSelfContainedEnquiryId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(CouncilTaxAnnexeSelfContainedEnquiryId, NormalMode)(new UserAnswers(cacheMap))))
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
}