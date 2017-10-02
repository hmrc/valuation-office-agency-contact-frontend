package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.$pluralName;format="lower"$

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.$className$Form
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.{Add$className$Id, $pluralName$Id}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Mode, $className$}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.$pluralName;format="lower"$.add$className$

import scala.concurrent.Future

class Add$className;format="cap"$Controller @Inject()(appConfig: FrontendAppConfig,
                                                  override val messagesApi: MessagesApi,
                                                  dataCacheConnector: DataCacheConnector,
                                                  navigator: Navigator,
                                                  authenticate: AuthAction,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (authenticate andThen getData andThen requireData) {
    implicit request =>
      Ok(add$className$(appConfig, $className$Form(), mode))
  }

  def onSubmit(mode: Mode) = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      $className$Form().bindFromRequest().fold(
        (formWithErrors: Form[$className$]) =>
          Future.successful(BadRequest(add$className$(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.addToCollection[$className$](request.externalId, $pluralName$Id.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(Add$className$Id, mode)(new UserAnswers(cacheMap))))
      )
  }
}
