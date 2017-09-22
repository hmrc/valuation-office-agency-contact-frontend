#!/bin/bash

if grep -Fxp ContactDetails applied
then
    echo "Migration ContactDetails has already been applied, exiting"
    exit 1
fi

echo "Applying migration ContactDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.Routes
echo "GET        /contactDetails                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.ContactDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.Routes
echo "POST       /contactDetails                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.ContactDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.Routes

echo "GET        /changeContactDetails                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.ContactDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.Routes
echo "POST       /changeContactDetails                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.ContactDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.Routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "contactDetails.title = contactDetails" >> ../conf/messages.en
echo "contactDetails.heading = contactDetails" >> ../conf/messages.en
echo "contactDetails.field1 = Field 1" >> ../conf/messages.en
echo "contactDetails.field2 = Field 2" >> ../conf/messages.en
echo "contactDetails.checkYourAnswersLabel = contactDetails" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def contactDetails: Option[ContactDetails] = cacheMap.getEntry[ContactDetails](ContactDetailsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def contactDetails: Option[AnswerRow] = userAnswers.contactDetails map {";\
     print "    x => AnswerRow(\"contactDetails.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.ContactDetailsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Registering this migration as completed"
echo "ContactDetails" >> applied
