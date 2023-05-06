import i18next from 'i18next';
import { useState } from 'react';

const langs = [
  {
    name: '🇫🇷 FR',
    key: 'fr',
  },
  {
    name: '🇬🇧 EN',
    key: 'en',
  },
];

function SwitchLang() {
  const [lang, setLang] = useState(i18next.language);

  const handleChange = (value: string) => {
    i18next.changeLanguage(value);
    setLang(value);
  };

  return (
    <div className="text-white bg-transparent border-white border-2 w-15 rounded-xl">
      <select
        defaultValue={lang}
        onChange={(value) => handleChange(value.currentTarget.value)}
        className="w-full h-full bg-transparent appearance-none text-center p-2"
      >
        {langs.map((l) => (
          <option value={l.key} key={l.key} className="bg-black">
            <div className="flex items-center">
              <span>{l.name}</span>
            </div>
          </option>
        ))}
      </select>
    </div>
  );
}

export default SwitchLang;
