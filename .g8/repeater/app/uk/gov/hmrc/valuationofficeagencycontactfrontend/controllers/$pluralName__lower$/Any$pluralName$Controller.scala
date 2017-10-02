package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.$pluralName;format="lower"$

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.BooleanForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.Any$pluralName$Id
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.Mode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.$pluralName;format="lower"$.any$pluralName$

import scala.concurrent.Future

class Any$pluralName$Controller @Inject()(appConfig: FrontendAppConfig,
                                         override val messagesApi: MessagesApi,
                                         dataCacheConnector: DataCacheConnector,
                                         navigator: Navigator,
                                         authenticate: AuthAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (authenticate andThen getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.any$pluralName$ match {
        case None => BooleanForm()
        case Some(value) => BooleanForm().fill(value)
      }
      Ok(any$pluralName$(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      BooleanForm().bindFromRequest().fold(
        (formWithErrors: Form[Boolean]) =>
          Future.successful(BadRequest(any$pluralName$(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[Boolean](request.externalId, Any$pluralName$Id.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(Any$pluralName$Id, mode)(new UserAnswers(cacheMap))))
      )
  }
}
