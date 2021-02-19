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

package uk.gov.hmrc.valuationofficeagencycontactfrontend

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{ContactDetails, PropertyAddress, TellUsMore}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

import scala.concurrent.ExecutionContext

trait SpecBase extends PlaySpec with GuiceOneAppPerSuite {

  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  def messages: Messages = messagesApi.preferred(fakeRequest)

  def servicesConfig: ServicesConfig = app.injector.instanceOf[ServicesConfig]

  implicit def ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  class FakeUserAnswers(cd: ContactDetails,
                        eq: String,
                        cts: String,
                        brs: String,
                        address: PropertyAddress,
                        tum: TellUsMore,
                        ae: String = "",
                        ee: Option[String] = None,
                        cacheMap: CacheMap = new CacheMap("", Map())) extends UserAnswers(cacheMap) {

    override def tellUsMore: Option[TellUsMore] = Some(tum)

    override def enquiryCategory: Option[String] = Some(eq)

    override def existingEnquiryCategory: Option[String] = ee

    override def councilTaxSubcategory: Option[String] = Some(cts)

    override def contactDetails: Option[ContactDetails] = Some(cd)

    override def businessRatesSubcategory: Option[String] = Some(brs)

    override def propertyAddress: Option[PropertyAddress] = Some(address)

    override def anythingElse: Option[String] = Some(ae)
  }
}
