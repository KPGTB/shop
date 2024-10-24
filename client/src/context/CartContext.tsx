import {
	createContext,
	PropsWithChildren,
	useCallback,
	useContext,
	useEffect,
	useState,
} from "react"

const CartContext = createContext<Cart>([[], (items: CartItem[]) => {}])

const getCart = () => {
	const items = localStorage.getItem("cart")
	return items !== null ? (JSON.parse(items) as CartItem[]) : []
}

const CartProvider = ({children}: PropsWithChildren) => {
	const [cartItems, setCartItemsState] = useState<CartItem[]>([])

	const setCartItems = useCallback((items: CartItem[]) => {
		setCartItemsState(items)
		localStorage.setItem("cart", JSON.stringify(items))
	}, [])

	useEffect(() => {
		setCartItems(getCart())
	}, [])

	return (
		<CartContext.Provider value={[cartItems, setCartItems]}>
			{children}
		</CartContext.Provider>
	)
}

const useCart = () => {
	return useContext<Cart>(CartContext)
}

export {useCart, CartProvider, getCart}
