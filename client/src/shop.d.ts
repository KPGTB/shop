type Category = {
	id: number
	name: string
	description: string
	nameInUrl: string
	productsId?: number[]
}
type TaxCode = {
	id: string
	name: string
	description: string
}

type FullCategory = {
	id: number
	name: string
	description: string
	nameInUrl: string
	products: Product[]
}

type Product = {
	id: number
	name: string
	nameInUrl: string
	description: string
	image: string
	currency: string
	price: number
	displayTax: number
	displayPrice: number
	taxCode: string
	categoryId: number
	fields: Field[]
}

type Field = {
	id?: number
	label: string
	optional: boolean
	type: "DROPDOWN" | "NUMERIC" | "TEXT"
	options: DropdownOption[]
	tId?: number
}

type DropdownOption = {
	id?: number
	label: string
	value: string
	tId?: number
}

type User = {
	email: string
	role: UserRole
	name: string
	surname: string
	birthDate: Date
}

type UserRole = "CUSTOMER" | "BUSINESS"

type ApiStatus = "loading" | "working" | "error"

type Auth = [
	boolean,
	User | undefined,
	AuthService | undefined,
	(newUser: User | undefined, newService: AuthService) => void
]

type Theme = "dark" | "light"

type AuthLoader = [User | undefined, AuthService]

type CartItem = {
	productId: number
	quantity: number
}

type Cart = [CartItem[], (items: CartItem[]) => void]
