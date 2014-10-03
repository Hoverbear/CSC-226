fn n(h: int) -> int {
    match h {
        h if h == 0 => 1,
        h if h == 1 => 2,
        h => 1 + n(h-1) + n(h-2)
    }
}

fn main() {
    println!("{}", n(9))
}

