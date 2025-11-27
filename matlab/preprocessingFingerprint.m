function [I_norm, I_orient, Gx, Gy, I_freq, I_enhanced, I_bin, I_thin, mask] = preprocessingFingerprint(I)
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here
% --- 1. Charge the image in grayscale ---

if size(I,3) == 3
    I = rgb2gray(I);
end
I = double(I);

% --- 2. Normalisation de l'image ---
M0 = 100; % Moyenne cible
VAR0 = 100; % Variance cible
I_norm = normalizeImg(I, M0, VAR0);

% --- 3. Estimation de l'orientation ---
w = 16; %blockSize
[I_orient, Gx, Gy] = computeOrientation(I_norm, w);
% 
% % --- 4. Estimation de la fréquence des crêtes ---
% I_freq = computeFrequency(I_norm, I_orient, blockSize);
% 
% % --- 5. Filtrage par Gabor (amélioration) ---
% I_enhanced = gaborEnhance(I_norm, I_orient, I_freq);
% 
% % --- 6. Binarisation ---
% I_bin = imbinarize(mat2gray(I_enhanced));
% 
% % --- 7. Erosion du bruit + amincissement (thinning) ---
% I_bin = bwmorph(I_bin, 'clean');
% I_bin = bwmorph(I_bin, 'spur', 5);
% I_thin = bwmorph(I_bin, 'thin', Inf);
% 
% % --- 8. Masque de l'empreinte (pour ignorer le fond) ---
% mask = imfill(I_bin, 'holes');

end

%% Fonctions utilitaires utilisées dans le processus principal

function I_norm = normalizeImg(I, M0, VAR0)
M = mean(I(:));
VAR = var(I(:));
I_norm = M0 + sqrt(VAR0/VAR)*abs(I-M);
end

function [orient, Gx, Gy] = computeOrientation(I, blockSize)
% Calcul des gradients : méthode classique de Sobel
Gx = imfilter(I, fspecial('sobel')');
Gy = imfilter(I, fspecial('sobel'));
[h, w] = size(I);

% Détermination du nombre de blocs
nBlocksY = floor(h/blockSize); nBlocksX = floor(w/blockSize);

% Initialisation des matrices
Vx = zeros(nBlocksY, nBlocksX);
Vy = zeros(nBlocksY, nBlocksX);

% Calcul de Vx et de Vy pour chaque bloc
% orient = zeros(rows, blockSize);
% filterSize = 5;
% lowPassFilter = zeros(filterSize, filterSize);
% lowPassFilter(:) = 1/filterSize^2;

for by = 1:nBlocksY
    for bx = 1:nBlocksX
        % Coordonnées du bloc
        yStart = (by-1)*blockSize + 1;
        yEnd = by * blockSize;
        xStart = (bx-1)*blockSize + 1;
        xEnd = bx*blockSize;

        % Extraire le bloc
        blockGx = Gx(yStart:yEnd, xStart:xEnd);
        blockGy = Gy(yStart:yEnd, xStart:xEnd);

        % Equations de l'article
        Vx(by, bx) = sum(sum(2 * blockGx .* blockGy));
        Vy(by, bx) = sum(sum(blockGx.^2 .* blockGy.^2));
        % val = atan2(Vy, Vx);
    end
end

% orient(i:i+blockSize-1, j:j+blockSize-1) = val;
% disp(orient(i+blockSize/2, j+blockSize/2))

% Lissage passe-bas sur Vx et Vy
% Hong & Jain recommandent un filtre passe-bas (ex: gaussien ou moyenneur)
% Dans le cas d'espère, on va utiliser un filtre gaussien 5x5 (standard)
hSmooth = fspecial('gaussian', [5 5], 1.5);

VxSmooth = conv2(Vx, hSmooth, 'same');
VySmooth = conv2(Vy, hSmooth, 'same');

% Orientation finale reconstruite
% Arctan2 pour obtenir l'angle 2*theta puis /2

orient = 0.5 * atan2(VxSmooth, VySmooth);

end

function freq = computeFrequency(I, orient, blockSize)
    [rows, cols] = size(I);
    freq = zeros(rows, cols);

    for i = 1:blockSize:rows-blockSize
        for j = 1:blockSize:cols-blockSize
            block = I(i:i+blockSize-1, j:j+blockSize-1);
            theta = orient(i,j);

            rotBlock = imrotate(block, -theta*180/pi, 'bilinear', 'crop');
            proj = sum(rotBlock, 1);

            peaks = find(diff(sign(diff(proj))) < 0);
            if length(peaks) > 1
                d = mean(diff(peaks));
                freq(i:i+blockSize-1, j:j+blockSize-1) = 1/d;
            end
        end
    end
end

function I_enh = gaborEnhance(I, orient, freq)
    [rows, cols] = size(I);
    I_enh = zeros(rows, cols);

    kx = 0.65;
    ky = 0.65;

    for i = 1:rows
        for j = 1:cols
            if freq(i,j) > 0
                f = freq(i,j);
                theta = orient(i,j);

                sigmaX = kx / f;
                sigmaY = ky / f;

                g = gaborKernel(sigmaX, sigmaY, f, theta);
                r = conv2(I, g, 'same');
                I_enh(i,j) = r(i,j);
            end
        end
    end
end

function g = gaborKernel(sx, sy, f, theta)
    sz = 8;
    [x, y] = meshgrid(-sz:sz, -sz:sz);

    xr = x * cos(theta) + y * sin(theta);
    yr = -x * sin(theta) + y * cos(theta);

    g = exp(-(xr.^2)/(2*sx^2) - (yr.^2)/(2*sy^2)) .* cos(2*pi*f*xr);
end

