#!/bin/bash

echo "Applying migration PropertyDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /propertyDetails                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.PropertyDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /propertyDetails                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.PropertyDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePropertyDetails                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.PropertyDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePropertyDetails                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.PropertyDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "propertyDetails.title = propertyDetails" >> ../conf/messages.en
echo "propertyDetails.heading = propertyDetails" >> ../conf/messages.en
echo "propertyDetails.field1 = Field 1" >> ../conf/messages.en
echo "propertyDetails.field2 = Field 2" >> ../conf/messages.en
echo "propertyDetails.checkYourAnswersLabel = propertyDetails" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def propertyDetails: Option[PropertyDetails] = cacheMap.getEntry[PropertyDetails](PropertyDetailsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def propertyDetails: Option[AnswerRow] = userAnswers.propertyDetails map {";\
     print "    x => AnswerRow(\"propertyDetails.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.PropertyDetailsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PropertyDetails completed"
