import {useState} from "react"
import {useLoaderData} from "react-router-dom"

import OrderModal from "../../../components/[account]/OrderModal/OrderModal"
import OrderImage from "../../../components/OrderImage/OrderImage"
import {toPrice} from "../../../util/PriceUtil"
import styles from "./OrdersPage.module.scss"

const OrdersPage = () => {
	const orders = useLoaderData() as OrderDto[]
	const [crrOrder, setCrrOrder] = useState<OrderDto | undefined>()
	return (
		<>
			<section className={styles.container}>
				<h1>Orders</h1>
				<section className={styles.orders}>
					{orders.map((order) => (
						<button
							className={styles.order}
							key={order.id}
							onClick={(el) => setCrrOrder(order)}
						>
							<section className={styles.images}>
								<OrderImage
									images={order.products.map(
										(p) => p.product!.image
									)}
								/>
							</section>
							<section className={styles.numbers}>
								<span
									className={
										styles[order.status] +
										" " +
										styles.status
									}
								>
									{order.status}
								</span>
								Order ID: {order.id}
								<br />
								Order Date:{" "}
								{order.orderDate
									? new Date(order.orderDate).toLocaleString()
									: "-"}
							</section>
							<section className={styles.products}>
								{order.products
									.map(
										(prod) =>
											`${prod.product!.name} x ${
												prod.quantity
											}`
									)
									.join(", ")}
							</section>
							<section className={styles.extra}>
								<table>
									<tr>
										<td>Subtotal</td>
										<td>
											{toPrice(order.subtotal)}{" "}
											{order.currency}
										</td>
									</tr>
									{order.discount > 0 && (
										<tr>
											<td>Discount</td>
											<td>
												-{toPrice(order.discount)}{" "}
												{order.currency}
											</td>
										</tr>
									)}
									{order.tax > 0 && (
										<tr>
											<td>Tax</td>
											<td>
												+{toPrice(order.tax)}{" "}
												{order.currency}
											</td>
										</tr>
									)}

									<hr />
									<tr>
										<td>Total</td>
										<td>
											{toPrice(order.total)}{" "}
											{order.currency}
										</td>
									</tr>
								</table>
							</section>
						</button>
					))}
				</section>
			</section>
			<OrderModal
				data={crrOrder}
				hide={() => setCrrOrder(undefined)}
			/>
		</>
	)
}

export default OrdersPage
