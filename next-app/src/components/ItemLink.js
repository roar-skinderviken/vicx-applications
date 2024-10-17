import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export default function ItemLink({ href, imgSrc, imgAlt, imgWidth, icon, text }) {
    return (
        <a href={href}
           className="flex items-center justify-center transition-transform duration-200 ease-in-out hover:scale-105 hover:shadow-lg mb-4 p-2"
        >
            <img
                src={imgSrc}
                alt={imgAlt}
                className="mr-3"
                width={imgWidth} />
            <FontAwesomeIcon icon={icon} className="fa-fw mr-2" />
            {text}
        </a>
    );
}