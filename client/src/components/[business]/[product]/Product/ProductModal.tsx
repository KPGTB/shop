import {useEffect, useState} from "react"
import {FaCoins, FaPlus, FaTrashCan} from "react-icons/fa6"
import {GiChoice} from "react-icons/gi"
import {LuFormInput} from "react-icons/lu"
import {
	MdDriveFileRenameOutline,
	MdEditDocument,
	MdImage,
	MdLink,
	MdOutlineDescription,
} from "react-icons/md"

import JsonForm from "../../../Form/JsonForm"
import Modal, {
	ModalButtons,
	ModalContent,
	ModalTitle,
} from "../../../layout/Modal/Modal"
import styles from "./Product.module.scss"

const ProductModal = ({
	visible,
	hide,
	category,
	taxes,
	data,
}: {
	visible: boolean
	hide: () => void
	category: number
	taxes: TaxCode[]
	data?: ProductDto
}) => {
	const [image, setImage] = useState<string>("")
	const [fields, setFields] = useState<ProductFieldDto[]>([])

	useEffect(() => {
		setImage(data !== undefined ? data.image : "")
		setFields(data !== undefined ? data.fields : [])
	}, [data])

	return (
		<Modal visible={visible}>
			<ModalTitle
				text={data !== undefined ? "Edit product" : "Add product"}
				closeAction={hide}
			/>
			<JsonForm
				method={data !== undefined ? "POST" : "PUT"}
				action={"/product"}
			>
				<ModalContent>
					{data !== undefined && (
						<input
							type="hidden"
							name="id"
							value={data.id}
						/>
					)}

					<input
						type="hidden"
						name="categoryId"
						value={category}
					/>

					<section className={styles.productModalImageContainer}>
						<aside>
							<img
								src={image}
								alt="Product image"
							/>
						</aside>

						<section>
							<label htmlFor="name">
								<MdDriveFileRenameOutline /> Name
							</label>
							<input
								id="name"
								name="name"
								placeholder={"Product name"}
								defaultValue={
									data !== undefined ? data.name : ""
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
								defaultValue={
									data !== undefined ? data.nameInUrl : ""
								}
								required
							/>

							<label htmlFor="price">
								<FaCoins /> Price
							</label>
							<input
								type="number"
								inputMode="numeric"
								step="0.01"
								id="price"
								name="price"
								placeholder={"Price"}
								defaultValue={
									data !== undefined ? data.price : ""
								}
								required
							/>

							<label htmlFor="displayTax">
								<FaCoins /> Display Tax
							</label>
							<input
								type="number"
								inputMode="numeric"
								step="0.01"
								id="displayTax"
								name="displayTax"
								placeholder={"Display Tax"}
								defaultValue={
									data !== undefined ? data.displayTax : ""
								}
								required
							/>

							<label htmlFor="image">
								<MdImage /> Image
							</label>
							<input
								id="image"
								type="file"
								onChange={(el) => {
									const data = new FormData()
									data.append("file", el.target.files![0])
									fetch(`${API_URL}/image/upload`, {
										method: "PUT",
										credentials: "include",
										body: data,
									})
										.then((res) => res.json())
										.then((json) => {
											setImage(json.data)
										})
								}}
							/>
							<input
								type="hidden"
								name="image"
								value={image}
							/>
						</section>
					</section>

					<label htmlFor="description">
						<MdOutlineDescription /> Description
					</label>
					<textarea
						rows={6}
						id="description"
						name="description"
						placeholder={"Product description"}
						defaultValue={
							data !== undefined ? data.description : ""
						}
						required
					/>

					<label htmlFor="tax">
						<MdEditDocument /> Tax Code{" "}
					</label>
					<select
						id="tax"
						name="taxCode"
					>
						{taxes.map((taxCode) => (
							<option
								key={taxCode.id}
								value={taxCode.id}
								selected={
									data !== undefined &&
									data.taxCode === taxCode.id
								}
							>
								{taxCode.name}
							</option>
						))}
					</select>

					<fieldset className={styles.productModalFields}>
						<legend>
							<LuFormInput /> Custom Fields
						</legend>

						{fields.map((field, idx) => (
							<FieldComp
								field={field}
								idx={idx}
								fields={fields}
								setFields={setFields}
							/>
						))}

						<button
							type="button"
							className={`icon ${styles.field}`}
							onClick={() => {
								fields.push({
									label: "",
									type: "TEXT",
									optional: false,
									optionsId: [],
									options: [],
									tId: new Date().getTime(),
								})
								setFields([...fields])
							}}
						>
							<FaPlus />
						</button>
					</fieldset>
				</ModalContent>
				<ModalButtons>
					<button>
						{data !== undefined ? "Edit" : "Add"} product
					</button>
				</ModalButtons>
			</JsonForm>
		</Modal>
	)
}

const FieldComp = ({
	field,
	idx,
	fields,
	setFields,
}: {
	field: ProductFieldDto
	idx: number
	fields: ProductFieldDto[]
	setFields: (fields: ProductFieldDto[]) => void
}) => {
	return (
		<section
			className={styles.field}
			key={"field_" + (field.id ? field.id : field.tId)}
		>
			{field.id && (
				<input
					type="hidden"
					name={`fields[${idx}][id]`}
					value={field.id}
				/>
			)}

			<section className={styles.fieldData}>
				<label className={styles.optionalLabel}>
					Optional
					<input
						type="checkbox"
						name={`fields[${idx}][optional]`}
						defaultChecked={field.optional}
						value={"true"}
					/>
				</label>

				<label className={styles.fieldRemove}>
					<button
						className={`icon`}
						type="button"
						onClick={() => {
							setFields([
								...fields.filter(
									(f) =>
										f.id !== field.id || f.tId !== field.tId
								),
							])
						}}
					>
						<FaTrashCan />
					</button>
				</label>

				<label>
					Label
					<input
						name={`fields[${idx}][label]`}
						defaultValue={field.label}
					/>
				</label>

				<label>
					Type
					<select
						name={`fields[${idx}][type]`}
						defaultValue={field.type}
						onChange={(el) => {
							field.type = el.target.value as
								| "DROPDOWN"
								| "NUMERIC"
								| "TEXT"
							setFields([...fields])
						}}
					>
						<option>DROPDOWN</option>
						<option>NUMERIC</option>
						<option>TEXT</option>
					</select>
				</label>
			</section>

			{field.type === "DROPDOWN" && (
				<fieldset className={styles.dropdownOptions}>
					<legend>
						<GiChoice /> Dropdown options
					</legend>
					{field.options.map((option, idx2) => (
						<OptionComp
							field={field}
							idx={idx}
							fields={fields}
							setFields={setFields}
							option={option}
							idx2={idx2}
						/>
					))}
					<button
						className="icon"
						type="button"
						onClick={() => {
							field.options.push({
								label: "",
								value: "",
								tId: new Date().getTime(),
							})
							setFields([...fields])
						}}
					>
						<FaPlus />
					</button>
				</fieldset>
			)}
		</section>
	)
}

const OptionComp = ({
	field,
	idx,
	fields,
	setFields,
	option,
	idx2,
}: {
	field: ProductFieldDto
	idx: number
	fields: ProductFieldDto[]
	setFields: (fields: ProductFieldDto[]) => void
	option: ProductFieldOptionDto
	idx2: number
}) => {
	return (
		<section
			className={styles.option}
			key={`field_${field.id ? field.id : field.tId}_option_${
				option.id ? option.id : option.tId
			}`}
		>
			{option.id && (
				<input
					type="hidden"
					name={`fields[${idx}][options[${idx2}][id]]`}
					value={option.id}
				/>
			)}

			<label htmlFor={`field_label_${idx}_option_${idx2}`}>Label</label>
			<input
				name={`fields[${idx}][options[${idx2}][label]]`}
				defaultValue={option.label}
				id={`field_label_${idx}_option_${idx2}`}
			/>
			<label htmlFor={`field_value_${idx}_option_${idx2}`}>Value</label>
			<input
				name={`fields[${idx}][options[${idx2}][value]]`}
				defaultValue={option.value}
				id={`field_value_${idx}_option_${idx2}`}
			/>
			<button
				className="icon"
				type="button"
				onClick={() => {
					field.options = field.options.filter(
						(o) => o.id !== option.id || o.tId !== option.tId
					)
					setFields([...fields])
				}}
			>
				<FaTrashCan />
			</button>
		</section>
	)
}

export default ProductModal
