import clsx from "clsx";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowDown, faArrowUp } from "@fortawesome/free-solid-svg-icons";
import { Text } from "./Text";
import { Button } from "./Button";

export default function UpDownVote({ className, value, vote } : { className : string, value : number, vote? : boolean | undefined }) : JSX.Element {
    return (
        <div className={clsx('flex-col justify-around items-center', className)}>
            {vote === true && (
                <div className="w-10 flex justify-center items-center">
                    <Button unstyled={true}>
                        <FontAwesomeIcon icon={faArrowUp} size="2xl" color="white" />
                    </Button>
                </div>
            )}
            {!vote && (
                 <div className="w-10 flex justify-center items-center">
                 <Button unstyled={true}>
                     <FontAwesomeIcon icon={faArrowUp} size="2xl" color="grey" />
                 </Button>
             </div>
            )}
            <Text as="p" color="white" centered={true} className={clsx('w-10 py-1')}>
                {value}
            </Text>
            {vote === false && (
                <div className="w-10 flex justify-center items-center">
                    <Button unstyled={true}>
                        <FontAwesomeIcon icon={faArrowDown} size="2xl" color="white" />
                    </Button>
                </div>
            )}
            { (vote === true || vote === null) && (
                <div className="w-10 flex justify-center items-center">
                <Button unstyled={true}>
                    <FontAwesomeIcon icon={faArrowDown} size="2xl" color="grey" />
                </Button>
            </div>
            )}
      </div>
    )
}