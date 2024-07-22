/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.pages

import play.api.i18n.Messages
import play.api.mvc.Call
import play.twirl.api.HtmlFormat.Appendable
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.journey.model.CustomizedContent
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.journey.customized.brPropertyOrAreaChangedInWales

/**
  * @author Yuriy Tumakha
  */
object BRPropertyOrAreaChangedInWales
  extends CustomizedContent(
    key = "property-or-area-changed-in-Wales",
    fieldId = "propertyOrAreaChangedInWales"
  ) {
  override def previousPage: UserAnswers => Call = _ => routes.JourneyController.onPageLoad(EnglandOrWalesPropertyRouter.key)

  override def template(customizedContent: CustomizedContent)(implicit messages: Messages): Appendable =
    brPropertyOrAreaChangedInWales(customizedContent)

}
