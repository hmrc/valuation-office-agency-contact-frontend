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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.handlers

import play.api.http.HttpErrorHandler
import play.api.test.FakeRequest
import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase
import play.api.test.Helpers._

class ErrorHandlerSpec extends SpecBase {

  def errorHandler = app.injector.instanceOf[HttpErrorHandler]

  "Error handler" should {
    "render error page" in {

      val result =  errorHandler.onClientError(
        FakeRequest(),404, "Not found")

      val content = contentAsString(result)

      content must include("Page not found")
      content must include("If you typed the web address, check it is correct.")
      content must include("If you pasted the web address, check you copied the entire address.")

    }
  }




}
