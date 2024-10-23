import {PropsWithChildren} from "react"
import {FaX} from "react-icons/fa6"

import styles from "./Modal.module.scss"

const Modal = ({visible, children}: {visible: boolean} & PropsWithChildren) => {
	return (
		<>
			<aside
				className={`${styles.bg} ${visible ? styles.visible : ""}`}
			/>
			<section
				className={`${styles.modal} ${visible ? styles.visible : ""}`}
			>
				{children}
			</section>
		</>
	)
}

const ModalTitle = ({
	text,
	closeAction,
}: {
	text: string
	closeAction: () => void
}) => {
	return (
		<section className={styles.title}>
			<h1>{text}</h1>{" "}
			<button
				className="icon"
				onClick={closeAction}
			>
				<FaX />
			</button>
		</section>
	)
}

const ModalContent = ({children}: PropsWithChildren) => {
	return <section className={styles.content}>{children}</section>
}

const ModalButtons = ({children}: PropsWithChildren) => {
	return <section className={styles.buttons}>{children}</section>
}

export default Modal
export {ModalTitle, ModalContent, ModalButtons}
