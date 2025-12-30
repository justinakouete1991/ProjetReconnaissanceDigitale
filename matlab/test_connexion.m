function I_norm = test_connexion(I)
% Son but est d'effectuer une opération élémentaire (la normalisation)
% sur une image pour tester la connectivité avec Java
%#codegen

w = 16;
I = double(I);

% Kernel moyenne
h = fspecial('average', w);

% Moyenne locale
mu = imfilter(I, h, 'replicate');

% Écart-type local
mu2 = imfilter(I.^2, h, 'replicate');
sigma = sqrt(mu2 - mu.^2 + eps);

targetMean = 0;
targetStd = 1;
I_norm = targetMean + targetStd * (I - mu) ./ (sigma + eps);

end

