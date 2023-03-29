import { Text } from '~/components';
import { Footer, Header } from '~/layout';

function AboutPage() {
  return (
    <>
      <Header className="h-16 my-1" />
      <div className="flex flex-col items-center w-full">
        <Text as="p" centered className="mt-14 md:w-1/2 w-3/4">
          Open Observatory est une application permettant de partager des observations d’objets célestes
        </Text>
        <Text as="p" centered className="mt-16 md:w-1/2 w-3/4">
          Si vous avez des questions, vous pouvez contacter le support à l’adresse suivante :
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
