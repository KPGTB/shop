import {useState} from "react"
import {FaPen, FaPlus, FaTrashCan} from "react-icons/fa6"

import ProductComp, {ProductCreate} from "../Product/ProductComp"
import ProductModal from "../Product/ProductModal"
import styles from "./Category.module.scss"

const CategoryComp = ({
	category,
	taxes,
	edit,
}: {
	category: CategoryDto
	taxes: TaxCode[]
	edit: () => void
}) => {
	const [productModalState, setProductModal] = useState<{
		visible: boolean
		data?: ProductDto
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
								headers: {
									"content-type": "application/json",
								},
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
					<ProductComp
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

				<ProductCreate
					create={() =>
						setProductModal({visible: true, category: category.id})
					}
				/>
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

const CategoryCreate = ({create}: {create: () => void}) => {
	return (
		<section className={styles.category}>
			<section>
				<h2>Create new category</h2>
			</section>
			<section className={styles.buttons}>
				<button
					className="icon"
					onClick={create}
				>
					<FaPlus />
				</button>
			</section>
		</section>
	)
}

export default CategoryComp
export {CategoryCreate}
