using Base.Threads
using DelimitedFiles

const N = 100
const T = N ÷ 4
A = zeros(Int, N, N)
X = zeros(Int, N, N)
B = zeros(Int, N, N)

function MultiplyAXParaleloReduction(k)
    m = size(A, 2)
    for i in 1:N
        temp = 0
        for j in 1:m
            temp += A[i, j] * X[j, k]
        end
        B[i, k] = temp
    end
end

function LoadData()
    a, b = -10, 10
    for i in 1:N
        for j in 1:N
            A[i, j] = rand(a:b)
            X[i, j] = rand(a:b)
        end
    end
end

function save_matrices()
    open("A.txt", "w") do io
        println(io, "Matriz A:")
        for i in 1:N
            println(io, join(A[i, :], ' '))
        end
    end

    open("X.txt", "w") do io
        println(io, "Matriz X:")
        for i in 1:N
            println(io, join(X[i, :], ' '))
        end
    end

    open("B.txt", "w") do io
        println(io, "Matriz B:")
        for i in 1:N
            println(io, join(B[i, :], ' '))
        end
    end
end

function LoadDataFromText()
    A_file = "A.txt"
    if isfile(A_file)
        A_data = readdlm(A_file, ' ')[2:end, :]  # Saltar la primera fila con el título
        global A
        A = convert(Array{Int}, A_data)
    else
        println("El archivo $A_file no existe.")
    end

    X_file = "X.txt"
    if isfile(X_file)
        X_data = readdlm(X_file, ' ')[2:end, :]  # Saltar la primera fila con el título
        global X
        X = convert(Array{Int}, X_data)
    else
        println("El archivo $X_file no existe.")
    end
end

function CompareResults()
    # Calcular el resultado esperado de la multiplicación de matrices A * X
    expected_result = A * X

    # Comparar los resultados obtenidos con los resultados esperados
    is_equal = isapprox(B, expected_result, atol=1e-6)  # Puedes ajustar la tolerancia según tus necesidades

    if is_equal
        println("Los resultados obtenidos son iguales a los resultados esperados.")
    else
        println("Los resultados obtenidos son diferentes de los resultados esperados.")
    end
end

function main()
    LoadData()
    @time begin
        @threads for k in 1:N
            MultiplyAXParaleloReduction(k)
        end
    end
    save_matrices()
    CompareResults()  # Comparar los resultados obtenidos con los esperados
end

main()
