using SharedArrays

function gram_schmidt_paralelo(A)
    m, n = size(A)
    Q = SharedArray{Float64}(A)
    R = SharedArray{Float64}(zeros(m, n))

    @sync for k in 1:n
        @async begin
            R[k, k] = norm(Q[:, k])
            Q[:, k] /= R[k, k]
        end

        @async begin
            for j in (k + 1):n
                R[k, j] = dot(Q[:, k], Q[:, j])
                Q[:, j] -= R[k, j] * Q[:, k]
            end
        end
    end

    return Q, R
end

function solve()
    # Primero, definimos la matriz
    A = [1 2 3; 4 5 6; 7 8 9]

    # Luego, llamamos a la función gram_schmidt_paralelo
    Q, R = gram_schmidt_paralelo(A)

    # Ahora, Q y R contienen la factorización QR de A
    display("Q = ", Q)
    display("R = ", R)
    display("A = ", Q*R)
end

solve()