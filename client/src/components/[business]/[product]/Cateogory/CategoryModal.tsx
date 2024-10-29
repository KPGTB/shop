import {
	MdDriveFileRenameOutline,
	MdLink,
	MdOutlineDescription,
} from "react-icons/md"

import JsonForm from "../../../Form/JsonForm"
import Modal, {
	ModalButtons,
	ModalContent,
	ModalTitle,
} from "../../../layout/Modal/Modal"

const CategoryModal = ({
	visible,
	hide,
	data,
}: {
	visible: boolean
	hide: () => void
	data?: CategoryDto
}) => {
	return (
		<Modal visible={visible}>
			<ModalTitle
				text={data !== undefined ? "Edit category" : "Add category"}
				closeAction={hide}
			/>
			<JsonForm
				method={data !== undefined ? "POST" : "PUT"}
				action={"/category"}
			>
				<ModalContent>
					{data !== undefined && (
						<input
							type="hidden"
							name="id"
							value={data.id}
						/>
					)}

					<label htmlFor="name">
						<MdDriveFileRenameOutline /> Name
					</label>
					<input
						id="name"
						name="name"
						placeholder={"Category name"}
						defaultValue={data !== undefined ? data.name : ""}
						required
					/>

					<label htmlFor="description">
						<MdOutlineDescription /> Description
					</label>
					<textarea
						rows={6}
						id="description"
						name="description"
						placeholder={"Category description"}
						defaultValue={
							data !== undefined ? data.description : ""
						}
						required
					/>

					<label htmlFor="url">
						<MdLink /> Name in URL
					</label>
					<input
						id="url"
						name="nameInUrl"
						placeholder={"name-in-url"}
						defaultValue={data !== undefined ? data.nameInUrl : ""}
						required
					/>
				</ModalContent>
				<ModalButtons>
					<button>
						{data !== undefined ? "Edit" : "Add"} category
					</button>
				</ModalButtons>
			</JsonForm>
		</Modal>
	)
}

export default CategoryModal
