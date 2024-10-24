import {FaPen, FaPlus, FaTrashCan} from "react-icons/fa6"

import styles from "./Product.module.scss"

const ProductComp = ({product, edit}: {product: Product; edit: () => void}) => {
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
							headers: {
								"content-type": "application/json",
							},
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

const ProductCreate = ({create}: {create: () => void}) => {
	return (
		<button
			className={`${styles.product} ${styles.addProduct}`}
			onClick={create}
		>
			<section className={styles.buttons}>
				<FaPlus />
			</section>
		</button>
	)
}

export default ProductComp
export {ProductCreate}
