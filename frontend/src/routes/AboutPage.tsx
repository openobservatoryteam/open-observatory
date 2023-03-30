import { useTranslation } from 'react-i18next';

import { Text } from '~/components';
import { Footer, Header } from '~/layout';

function AboutPage() {
  const { t } = useTranslation();
  return (
    <>
      <Header className="h-16 my-1" />
      <div className="flex flex-col items-center">
        <Text as="p" centered className="mt-14 md:w-1/2 w-3/4">
          {t('common.descriptionOpenObservatory')}
        </Text>
        <Text as="p" centered className="mt-16 md:w-1/2 w-3/4">
          {t('common.questions')}
        </Text>
        <Text as="span" centered className="mt-16 md:w-1/2 w-3/4">
          support.openobservatory@gmail.com
        </Text>
      </div>
      <Footer />
    </>
  );
}

export default AboutPage;
