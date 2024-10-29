import Steps from "rc-steps"
import {
	FaDatabase,
	FaFileInvoiceDollar,
	FaMoneyBill,
	FaReceipt,
	FaStripeS,
} from "react-icons/fa6"
import {MdEmail, MdPhone} from "react-icons/md"

import {toPrice} from "../../../util/PriceUtil"
import Modal, {
	ModalButtons,
	ModalContent,
	ModalTitle,
} from "../../layout/Modal/Modal"
import styles from "./OrderModal.module.scss"

const OrderModal = ({hide, data}: {hide: () => void; data?: OrderDto}) => {
	return (
		<Modal visible={data !== undefined}>
			<ModalTitle
				text={`Order Information (ID: ${data?.id})`}
				closeAction={hide}
			/>
			<ModalContent>
				<section>
					<Steps
						items={[
							{
								title: "Order placed",
								description: `${new Date(
									data?.orderDate!
								).toLocaleString()} - Order has been placed`,
							},
							{
								title: "Paying",
								description: "Waiting for payment",
							},
							{
								title: "Paid",
								description: `${
									data?.paymentDate
										? new Date(
												data?.paymentDate
										  ).toLocaleString()
										: "Waiting"
								} - Payment has been accepted`,
							},
							{
								title: "Completed",
								description: `${
									data?.completionDate
										? new Date(
												data?.completionDate
										  ).toLocaleString()
										: "Waiting"
								} - Order completed`,
							},
						]}
						current={
							data?.status === "PAYING" ||
							data?.status === "FAILED"
								? 1
								: data?.status === "PAID"
								? 2
								: 3
						}
						status={
							data?.status === "FAILED"
								? "error"
								: data?.status === "PAYING"
								? "process"
								: "finish"
						}
					/>
				</section>

				<section className={styles.infoContainer}>
					<section>
						<h2>Customer details</h2>
						<p>
							<MdEmail /> {data?.orderEmail || "-"}
						</p>
						<p>
							<MdPhone /> {data?.phoneNumber || "-"}
						</p>
						<hr />

						<h4>Billing details</h4>

						<p>{data?.customer}</p>
						<p>{data?.address1}</p>
						<p>{data?.address2}</p>
						<p>
							{data?.postalCode} {data?.city}
						</p>
						<p>
							{data?.state}
							{data?.state && ", "}
							{data?.country}
						</p>

						<hr />
						<h4>Account details</h4>
						{data?.user
							? `Bought by user ${data?.user.email} (${data.user.id})`
							: `Bought by annonymous user`}
					</section>
					<section>
						<h2>Identification numbers</h2>
						<p>
							<FaDatabase /> ID: {data?.id}
						</p>
						<p>
							<FaStripeS /> Stripe ID: {data?.stripeId}
						</p>
						<p>
							<FaFileInvoiceDollar /> Invoice number:{" "}
							{data?.invoiceNumber || "-"}
						</p>
					</section>
				</section>

				<section className={styles.productsContainer}>
					<section>
						<h3>Products</h3>

						{data?.products!.map((op) => (
							<section
								className={styles.product}
								key={`op_${op.id}`}
							>
								<img
									src={op.product?.image}
									alt="pid"
								/>
								<section className={styles.productInfo}>
									<h4>
										{op.product?.name} x {op.quantity}
									</h4>
									<section className={styles.fields}>
										{op.fields.map((field) => (
											<p>
												<b>{field.field?.label}</b> -{" "}
												<i>
													{field.value ||
														"No information provided"}
												</i>
											</p>
										))}
									</section>
									<p className={styles.productPrice}>
										{toPrice(
											op.product?.displayPrice! *
												op.quantity
										)}{" "}
										{data?.currency}
									</p>
								</section>
							</section>
						))}
					</section>
					<section>
						<h3>Price</h3>

						<table>
							<tr>
								<td>Subtotal</td>
								<td>
									{toPrice(data?.subtotal)} {data?.currency}
								</td>
							</tr>
							{(data?.discount || 0) > 0 && (
								<tr>
									<td>Discount</td>
									<td>
										-{toPrice(data?.discount)}{" "}
										{data?.currency}
									</td>
								</tr>
							)}
							{(data?.tax || 0) > 0 && (
								<tr>
									<td>Tax</td>
									<td>
										+{toPrice(data?.tax)} {data?.currency}
									</td>
								</tr>
							)}

							<hr />
							<tr>
								<td>Total</td>
								<td>
									{toPrice(data?.total)} {data?.currency}
								</td>
							</tr>
						</table>
					</section>
				</section>
			</ModalContent>
			<ModalButtons>
				{data?.status === "PAID" || data?.status === "COMPLETED" ? (
					<>
						<a
							href={`${API_URL}/payment/document/invoice/${data?.invoiceNumber}`}
							target="_blank"
							className="buttonLike"
						>
							<FaFileInvoiceDollar /> Download Invoice
						</a>
						<a
							href={`${API_URL}/payment/document/receipt/${data?.invoiceNumber}`}
							target="_blank"
							className="buttonLike"
						>
							<FaReceipt /> Download Receipt
						</a>
					</>
				) : data?.status === "PAYING" ? (
					<a
						href={data.paymentUrl}
						target="_blank"
						className="buttonLike"
					>
						<FaMoneyBill /> Pay
					</a>
				) : (
					"Order failed. Please try again later"
				)}
			</ModalButtons>
		</Modal>
	)
}

export default OrderModal
