function NormVector(V)
   n = length(V);
   S = 0;
   for i = 1:n
       S = S + (V[i]^2);
   end
   return sqrt(S);
end
#-----------------------------------------------
function NormMatrix(A)
   m,n = size(A);
   S = 0;
   for i = 1:m
       for j = 1:n
           S = S + (A[i,j]^2);
       end
   end
   return sqrt(S);
end
#-----------------------------------------------
function Factorization_QR(A)
   m,n = size(A);
   Q = zeros(n,n);
   R = zeros(n,n);
   Q[:,1]=(1/norm(A[:,1]))*A[:,1];
   for j = 2:n
       P = zeros(n);
       for k = 1:(j-1)
           P  = P + (A[:,j]'*Q[:,k])*Q[:,k];
       end
       D = A[:,j] - P;
       Q[:,j]=(1/norm(D))*D;
   end
   R = Q'A;
   return Q, R
end
#-----------------------------------------------
using Base.Threads

function Factorization_QR_Paralelo(A)
    m,n = size(A);
    Q = zeros(n,n);
    R = zeros(n,n);

    @threads for i in 1:n
        Q[:, i] = (1 / norm(A[:, i])) * A[:, i]
    end

    for j = 2:n
        P = zeros(n);

        for k = 1:(j-1)
            if length(threads) < num_threads
                push!(threads, Threads.@spawn begin
                    P  = P + (A[:,j]'*Q[:,k])*Q[:,k];
                end)
            else
                wait(threads[1])
                popfirst!(threads)
            end
        end

        D = A[:,j] - P;

        @threads for j in 1:n
            Q[:,j]=(1/norm(D))*D;
        end
    end
    R = Q'A;
    return Q, R
 end
#-----------------------------------------------
function GaussJordan(A,B)
   m,n = size(A);
   X = zeros(n);
   for j = 1:n
       for i = 1:n
           E = -1*(A[i,j]/A[j,j]);
           if i!=j
              A[i,:] = A[i,:] + E*A[j,:];
              B[i]   = B[i]   + E*B[j];
           end
       end
   end
   for j = 1:n
       X[j] = B[j]/A[j,j];
   end
   return X,A,B;
end
#-----------------------------------------------
function ReglaCramer(A,B,X)
   m,n = size(A)
   X = zeros(n)


   D = det(A)
   for k = 1:n


       T = zeros(m,n)
       for i = 1:m
           for j = 1:n
               T[i,j] = A[i,j]
           end
       end


              T[:,k] = B;
       X[k] = det(T)/D
   end
   return X
end








#-----------------------------------------------


#-----------------------------------------------
#-----------------------------------------------