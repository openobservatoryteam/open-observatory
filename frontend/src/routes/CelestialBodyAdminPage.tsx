import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link } from '@tanstack/react-location';
import { useOverlayTriggerState } from 'react-stately';
import { faCamera, faSave } from '@fortawesome/free-solid-svg-icons';

import celestialBodyImage from '@/assets/png/celeste.png';
import { Button, List, Text, Title, Modal, Dialog, TextInput, Slider } from '@/components';
import { Logo } from '@/assets';
import { ChangeEvent, useState } from 'react';

const fake = [
  { id: 1, iconURL: celestialBodyImage, name: 'Galaxie Messier', validityTime: 180 },
  { id: 2, iconURL: celestialBodyImage, name: 'Messmer', validityTime: 240 },
  { id: 3, iconURL: celestialBodyImage, name: 'Kevin', validityTime: 120 },
  { id: 4, iconURL: celestialBodyImage, name: 'Galaxie Messier', validityTime: 180 },
  { id: 5, iconURL: celestialBodyImage, name: 'Messmer', validityTime: 240 },
  { id: 6, iconURL: celestialBodyImage, name: 'Kevin', validityTime: 120 },
] as const;

function CelestialBodyAdminPage() {
  const state = useOverlayTriggerState({});

  const [value, setValue] = useState(1);
  const [image, setImage] = useState<any>();


  const handleChange = (evt : ChangeEvent<HTMLInputElement>) => {
    if (evt.target.files && evt.target.files[0]) {
      let reader = new FileReader();
      reader.onload = (e) => {
        setImage(e.target?.result)
      }
      reader.readAsDataURL(evt.target.files[0])
    }
  }
 
  return (
    <div className="flex">
      <aside className="bg-[#333C47] min-h-screen pt-4 px-3 md:px-12">
        <Link title="Accueil Open Observatory" to="/">
          <Logo />
        </Link>
        <Text centered className="mb-20 mt-4">
          ADMINISTRATEUR
        </Text>
        <ul>
          <li>
            <Button
              as={Link}
              className="bg-transparent mb-12 py-4 text-white text-xl hover:bg-white hover:opacity-80 hover:text-black"
              to="/admin/observations"
            >
              Observations
            </Button>
          </li>
          <li>
            <Button as={Link} className="mb-12 py-4 text-xl" color="white" to="/admin/celestial-bodies">
              Objets célestes
            </Button>
          </li>
          <li>
            <Button
              as={Link}
              className="bg-transparent mb-12 py-4 text-white text-xl hover:bg-white hover:opacity-80 hover:text-black"
              to="/admin/users"
            >
              Utilisateurs
            </Button>
          </li>
        </ul>
      </aside>
      <div className="flex-1">
        <Title as="h2" centered className="mt-4">
          Objets célestes
        </Title>
        <div className="flex justify-end mb-8 mt-2 mx-4 md:mx-16">
          <Button className="p-4" color="darkGray" onPress={state.open} rounded>
            <FontAwesomeIcon icon={faPlus} size="lg" />
          </Button>
        </div>
        <Modal isDismissable state={state}>
          <Dialog title="Création d'un objet céleste">
            <form className="flex items-center flex-col">
            {image && 
              <div className='relative w-96 h-52 flex justify-center items-center rounded-2xl'>
                <img src={image} className="rounded-2xl" />
                <FontAwesomeIcon icon={faCamera} size="lg" color='black' className='rounded-full p-3 bg-[#D9D9D9] absolute bottom-0 right-5'/>
              </div>
            }
            {!image && 
              <label className="cursor-pointer py-10 bg-[#D9D9D9] w-96 h-52 flex justify-center items-center rounded-2xl">
              <input type="file" className='hidden' onChange={(evt) => handleChange(evt)} />
               <FontAwesomeIcon icon={faCamera} size="5x" color='black'/>
              </label>
            }
            <div className="mt-8 w-full flex items-center justify-center">
              <Text as='span' className='mr-5'>
                Nom
              </Text>
              <TextInput name="name" className='w-1/2' placeholder="Nom de l'objet céleste"/>
            </div>
            <div className="mt-8 w-full flex items-center justify-center">
              <Text as='span' className='mr-5'>
                Validité
              </Text>
              <Slider minValue={1} maxValue={10} withMarks className='w-1/3' step={1} value={value} onChange={(val) =>  setValue(val)}/>
              <Text as='span' className='ml-5'>
                {value} {value > 1 ? ' heures' : ' heure'}
              </Text>
            </div>
            <Button rounded onPress={state.close} className='p-2 mt-10 flex justify-between w-1/4'>
              Enregistrer
              <FontAwesomeIcon icon={faSave} size="1x" color='black'/>
            </Button>
            </form>
          </Dialog>
        </Modal>
        <List
          className="mx-4 md:mx-16"
          data={fake}
          pageCount={3}
          render={(e) => (
            <Button
              className="active:brightness-90 bg-white flex flex-col gap-4 items-center rounded-2xl"
              color="white"
              key={e.id}
              unstyled
            >
              <img className="rounded-t-2xl" src={e.iconURL} alt={`Illustration de ${e.name}`} />
              <Text color="black">{e.name}</Text>
              <Text className="mb-3" color="black">
                Validité : {e.validityTime}h
              </Text>
            </Button>
          )}
        />
      </div>
    </div>
  );
}

export default CelestialBodyAdminPage;
