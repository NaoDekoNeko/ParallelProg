using DelimitedFiles

const N = Int(100)

function MultiplyAXSerial(k)
    m = size(A, 2)
    for i in 1:N
        temp = zeros(Int, m)
        for j in 1:m
            temp[j] = A[i, j] * X[j, k]
        end
        for l in Int(floor(log2(m))):-1:0
            for j in 1:(Int(2^l))
                if (j + Int(2^l)) <= m
                    temp[j] += temp[j + Int(2^l)]
                end
            end
        end
        B[i, k] = temp[1]
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
    open("Bs.txt", "w") do io
        println(io, "Matriz B:")
        for i in 1:N
            println(io, join(B[i, :], ' '))
        end
    end
end 

function LoadDataFromText()
    # Cargar la matriz A desde el archivo "A.txt"
    A_file = "A.txt"
    if isfile(A_file)
        A_data = readdlm(A_file, ' ')[2:end, :]  # Saltar la primera fila con el título
        global A
        A = convert(Array{Int}, A_data)
    else
        println("El archivo $A_file no existe.")
    end

    # Cargar la matriz X desde el archivo "X.txt"
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
    LoadDataFromText()
    @time begin
        for k in 1:N
            MultiplyAXSerial(k)
        end
    end
    save_matrices()
    CompareResults()  # Comparar los resultados obtenidos con los esperados
end

main()