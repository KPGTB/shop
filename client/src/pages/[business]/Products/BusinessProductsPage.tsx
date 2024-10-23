import {useEffect, useState} from "react"
import {FaCoins, FaPen, FaPlus, FaTrashCan} from "react-icons/fa6"
import {
	MdDriveFileRenameOutline,
	MdEditDocument,
	MdImage,
	MdLink,
	MdOutlineDescription,
} from "react-icons/md"
import {useLoaderData} from "react-router-dom"

import FetchForm from "../../../components/[Form]/FetchForm"
import JsonForm from "../../../components/[Form]/JsonForm"
import {Category} from "../../../components/layout/Header/HeaderLoader"
import Modal, {
	ModalButtons,
	ModalContent,
	ModalTitle,
} from "../../../components/layout/Modal/Modal"
import {Field, FullCategory, Product, TaxCode} from "./BusinessProductsLoader"
import styles from "./BusinessProductsPage.module.scss"

const BusinessProductsPage = () => {
	const [categories, taxes] = useLoaderData() as [FullCategory[], TaxCode[]]

	const [categoryModalState, setCategoryModal] = useState<{
		visible: boolean
		data?: Category
	}>({visible: false, data: undefined})

	return (
		<>
			{categories.map((category) => (
				<CategoryEl
					category={category}
					key={category.id}
					taxes={taxes}
					edit={() =>
						setCategoryModal({visible: true, data: category})
					}
				/>
			))}

			<section className={styles.category}>
				<section>
					<h2>Create new category</h2>
				</section>
				<section className={styles.buttons}>
					<button
						className="icon"
						onClick={() =>
							setCategoryModal({visible: true, data: undefined})
						}
					>
						<FaPlus />
					</button>
				</section>
			</section>

			<CategoryModal
				visible={categoryModalState.visible}
				data={categoryModalState.data}
				hide={() => setCategoryModal({visible: false, data: undefined})}
			/>
		</>
	)
}

const CategoryEl = ({
	category,
	taxes,
	edit,
}: {
	category: FullCategory
	taxes: TaxCode[]
	edit: () => void
}) => {
	const [productModalState, setProductModal] = useState<{
		visible: boolean
		data?: Product
		category: number
	}>({visible: false, category: 0})

	return (
		<>
			<section className={styles.category}>
				<section>
					<h2>{category.name}</h2>
					<p className={styles.url}>
						/{category.nameInUrl}.{category.id}
					</p>
					<p className={styles.categoryDesc}>
						{category.description}
					</p>
				</section>
				<section className={styles.buttons}>
					<button
						className="icon"
						onClick={edit}
					>
						<FaPen />
					</button>
					<button
						className="icon"
						onClick={() =>
							fetch(`${API_URL}/category`, {
								method: "DELETE",
								credentials: "include",
								body: JSON.stringify(category.id),
							}).then(() => window.location.reload())
						}
					>
						<FaTrashCan />
					</button>
				</section>
			</section>
			<section className={styles.products}>
				{category.products.map((product) => (
					<ProductEl
						product={product}
						key={`p_${product.id}`}
						edit={() =>
							setProductModal({
								visible: true,
								data: product,
								category: category.id,
							})
						}
					/>
				))}

				<button
					className={`${styles.product} ${styles.addProduct}`}
					onClick={() =>
						setProductModal({visible: true, category: category.id})
					}
				>
					<section className={styles.buttons}>
						<FaPlus />
					</section>
				</button>
			</section>

			<ProductModal
				visible={productModalState.visible}
				data={productModalState.data}
				category={productModalState.category}
				taxes={taxes}
				hide={() => setProductModal({visible: false, category: 0})}
			/>
		</>
	)
}

const CategoryModal = ({
	visible,
	hide,
	data,
}: {
	visible: boolean
	hide: () => void
	data?: Category
}) => {
	return (
		<Modal visible={visible}>
			<ModalTitle
				text={data !== undefined ? "Edit category" : "Add category"}
				closeAction={hide}
			/>
			<FetchForm
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
			</FetchForm>
		</Modal>
	)
}

const ProductEl = ({product, edit}: {product: Product; edit: () => void}) => {
	return (
		<section className={styles.product}>
			<img
				src={product.image}
				alt="Product image"
			/>
			<h3>{product.name}</h3>
			<p className={styles.url}>
				/.../
				{product.nameInUrl}.{product.id}
			</p>
			<p>
				{product.price} {product.currency}
			</p>
			<section className={styles.buttons}>
				<button
					className="icon"
					onClick={edit}
				>
					<FaPen />
				</button>
				<button
					className="icon"
					onClick={() =>
						fetch(`${API_URL}/product`, {
							method: "DELETE",
							credentials: "include",
							body: JSON.stringify(product.id),
						}).then(() => window.location.reload())
					}
				>
					<FaTrashCan />
				</button>
			</section>
		</section>
	)
}

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
	data?: Product
}) => {
	const [image, setImage] = useState<string>("")
	const [fields, setFields] = useState<Field[]>([])

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
						<MdEditDocument /> Tax Code
					</label>
					<select
						id="tax"
						name="taxCode"
						defaultValue={data !== undefined ? data.taxCode : ""}
					>
						{taxes.map((taxCode) => (
							<option
								key={taxCode.id}
								value={taxCode.id}
							>
								{taxCode.name}
							</option>
						))}
					</select>

					<fieldset>
						<legend>Custom Fields</legend>

						{fields.map((field, idx) => (
							<section
								className={styles.field}
								key={"field_" + idx}
							>
								<input
									type="hidden"
									name={`fields[${idx}][id]`}
									value={field.id}
								/>
								<section>
									<label htmlFor={`field_label_${idx}`}>
										Label
									</label>
									<input
										name={`fields[${idx}][label]`}
										defaultValue={field.label}
										id={`field_label_${idx}`}
									/>

									<label htmlFor={`field_type_${idx}`}>
										Type
									</label>
									<select
										name={`fields[${idx}][type]`}
										id={`field_type_${idx}`}
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

									<label htmlFor={`field_optional_${idx}`}>
										Optional
									</label>
									<input
										type="checkbox"
										name={`fields[${idx}][optional]`}
										defaultChecked={field.optional}
										id={`field_optional_${idx}`}
									/>
								</section>

								{field.type === "DROPDOWN" && (
									<>
										{field.options.map((option, idx2) => (
											<section
												className={styles.option}
												key={`field_${idx}_option_${idx2}`}
											>
												<input
													type="hidden"
													name={`fields[${idx}][options[${idx2}][id]]`}
													value={option.id}
												/>

												<label
													htmlFor={`field_label_${idx}_option_${idx2}`}
												>
													Label
												</label>
												<input
													name={`fields[${idx}][options[${idx2}][label]]`}
													defaultValue={option.label}
													id={`field_label_${idx}_option_${idx2}`}
												/>
												<label
													htmlFor={`field_value_${idx}_option_${idx2}`}
												>
													Value
												</label>
												<input
													name={`fields[${idx}][options[${idx2}][value]]`}
													defaultValue={option.value}
													id={`field_value_${idx}_option_${idx2}`}
												/>
											</section>
										))}
										<button
											className="icon"
											type="button"
											onClick={() => {
												field.options.push({
													id: -1,
													label: "",
													value: "",
												})
												setFields([...fields])
											}}
										>
											<FaPlus />
										</button>
									</>
								)}
							</section>
						))}

						<button
							type="button"
							className={styles.field}
							onClick={() => {
								fields.push({
									id: -1,
									label: "",
									type: "TEXT",
									optional: false,
									options: [],
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

export default BusinessProductsPage
