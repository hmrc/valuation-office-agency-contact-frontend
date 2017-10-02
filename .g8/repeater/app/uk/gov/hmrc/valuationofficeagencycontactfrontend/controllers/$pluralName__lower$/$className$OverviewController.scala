package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.$pluralName;format="lower"$

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{AuthAction, DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{$className$OverviewId, $pluralName$Id}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.$className$OverviewViewModel
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.$pluralName;format="lower"$.$className;format="decap"$Overview
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

class $className$OverviewController @Inject()(val appConfig: FrontendAppConfig,
                                          val messagesApi: MessagesApi,
                                          navigator: Navigator,
                                          authenticate: AuthAction,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (authenticate andThen getData andThen requireData) {
    implicit request =>
      val viewModel = $className$OverviewViewModel(
        request.userAnswers.$pluralName;format="decap"$.getOrElse(Seq()),
        routes.Add$className$Controller.onPageLoad(mode),
        navigator.nextPage($className$OverviewId, mode)(request.userAnswers))
      Ok($className;format="decap"$Overview(appConfig, viewModel, mode))
  }
}
