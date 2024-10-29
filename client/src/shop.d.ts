type UserDto = {
	id: number
	email: string
	active: boolean
	role: UserRole
}
type UserRole = "CUSTOMER" | "BUSINESS"

type EmailTemplateDto = {
	id: number
	type: string
	subject: string
	content: string
}

type ProductFieldOptionDto = {
	id?: number
	label: string
	value: string
} & Creatable

type ProductFieldDto = {
	id?: number
	label: string
	optional: boolean
	type: "DROPDOWN" | "NUMERIC" | "TEXT"
	optionsId: number[]
	options: ProductFieldOptionDto[]
} & Creatable

type ProductDto = {
	id: number
	name: string
	nameInUrl: string
	description: string
	image: string
	currency: string
	price: number
	taxCode: string
	displayTax: number
	displayPrice: number
	stripeId: string
	categoryId: number
	category?: CategoryDto
	fieldsId: number[]
	fields: ProductFieldDto[]
}

type CategoryDto = {
	id: number
	name: string
	description: string
	nameInUrl: string
	productsId: number[]
	products: ProductDto[]
}

type OrderProductDto = {
	id: number
	productId: number
	product?: ProductDto
	quantity: number
	fieldsId: number[]
	fields: OrderFieldDto[]
}

type OrderFieldDto = {
	id: number
	fieldId: number
	field?: ProductFieldDto
	value: string
}

type OrderDto = {
	id: number
	userId?: number
	user?: UserDto
	orderEmail: string
	customer: string
	phoneNumber?: string
	country?: string
	state?: string
	address1?: string
	address2?: string
	postalCode?: string
	city?: string
	productsId: number[]
	products: OrderProductDto[]
	status: "PAYING" | "PAID" | "COMPLETED" | "FAILED"
	stripeId?: string
	paymentUrl?: string
	invoiceNumber?: string
	total: number
	subtotal: number
	tax: number
	discount: number
	currency: string
	orderDate?: number
	paymentDate?: number
	completionDate?: number
}

type Creatable = {
	tId?: number
}

type TaxCode = {
	id: string
	name: string
	description: string
}

type ApiStatus = "loading" | "working" | "error"

type Auth = [
	boolean,
	User | undefined,
	AuthService | undefined,
	(newUser: User | undefined, newService: AuthService) => void
]
type AuthLoader = [User | undefined, AuthService]

type Theme = "dark" | "light"

type CartItem = {
	productId: number
	quantity: number
}
type Cart = [CartItem[], (items: CartItem[]) => void]
