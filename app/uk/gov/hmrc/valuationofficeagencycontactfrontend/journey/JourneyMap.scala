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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.journey

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.NotFound
import play.api.mvc.{ActionRefiner, Request, Result, WrappedRequest}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.FrontendAppConfig
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.pages._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.requests.DataRequest
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.page_not_found

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
 * @author Yuriy Tumakha
 */
@Singleton
class JourneyMap @Inject()(pageNotFound: page_not_found,
                           appConfig: FrontendAppConfig,
                           override val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends I18nSupport {

  private val pages: Seq[Page[String]] = Seq(
    // Housing Benefit, Local Housing Allowances
    HousingBenefitAllowancesRouter,
    LocalHousingAllowanceRates,
    HousingBenefitEnquiry,
    OtherHAHBEnquiry,
    HousingBenefitAppeals,
    HBTellUsMore,
    OtherHBEnquiry
  )

  val journeyMap: Map[String, Page[String]] = pages.map(page => page.key -> page).toMap

  def getPage(key: String) = new ActionRefiner[DataRequest, JourneyPageRequest] {
    def executionContext: ExecutionContext = ec

    def refine[A](request: DataRequest[A]): Future[Either[Result, JourneyPageRequest[A]]] = Future.successful {
      implicit val req: Request[A] = request.request

      journeyMap
        .get(key)
        .map(JourneyPageRequest(_, request.request, request.sessionId, request.userAnswers))
        .toRight(NotFound(pageNotFound(appConfig)))
    }
  }

}
