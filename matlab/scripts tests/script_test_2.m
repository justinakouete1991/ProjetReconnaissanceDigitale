I1 = imread('..\data\images\101_1.tif');
I2 = imread('..\data\images\101_2.tif');

% I1 = rgb2gray(I1);
% I2 = rgb2gray(I2);

I1 = double(I1);
I2 = double(I2);

figure; imshow(I1,[]); title('Image originale I1');

N1 = normalizeLocal(I1);
N2 = normalizeLocal(I2);
figure; imshow(N1,[]); title('Normalisation locale N1');

mask1 = segmentCoherence(N1);
mask2 = segmentCoherence(N2);
figure; imshow(mask1); title('Masque de segmentation');

O1 = orientationTensor(N1);
O2 = orientationTensor(N2);
figure; showOrientationField(O1, mask1); title('Champ d''orientation');

% Lissage passe-bas
O1 = imgaussfilt(O1, 3);
O2 = imgaussfilt(O2, 3);

F1 = ridgeFrequency(N1, O1);
F2 = ridgeFrequency(N2, O2);
figure; imshow(F1,[]); title('Carte de fréquence (F1)');

E1 = gaborEnhanced(N1, O1, F1, mask1);
E2 = gaborEnhanced(N2, O2, F2, mask2);
figure; imshow(E1,[]); title('Image améliorée (Gabor)');

B1 = imbinarize(E1);
B2 = imbinarize(E2);
figure; imshow(B1,[]); title('Image binarisée');

S1 = bwmorph(B1, 'thin', Inf);
S2 = bwmorph(B2, 'thin', Inf);
figure; imshow(S1,[]); title('Squelette (thinning)');

M1 = extractMinutiae(S1);
M2 = extractMinutiae(S2);
figure;
subplot(1,2,1); showMinutiae(S1, M1); title('Minuties détectées Image 1');
subplot(1,2,2); showMinutiae(S2, M2); title('Minuties détectées Image 2');

[score, bestDelta] = matchMinutiae(M1, M2);

figure; imshow(I1,[]); hold on;
alignedM1 = M1(:,1:2) + bestDelta;
plot(alignedM1(:,1), alignedM1(:,2),'ro');
plot(M2(:,1),       M2(:,2),      'go');
title(sprintf('Matching visuel - Score = %.2f', score));
hold off

fprintf("Score de similarité = %.4f\n", score);

if score > 0.35
    disp("Empreintes MATCH");
else
    disp("Empreintes DIFFERENTES");
end

%% Fonctions utilitaires

function [score, bestDelta] = matchMinutiae(M1, M2)

  if isempty(M1) || isempty(M2), score=0; bestDelta = [0 0]; return; end
    best = 0;
    bestDelta = [0 0];
for k = 1:500 % essais RANSAC
    idx1 = randi(size(M1,1));
    idx2 = randi(size(M2,1));

    p1 = M1(idx1,1:2);
    p2 = M2(idx2,1:2);

    delta = p2 - p1;

    aligned = M1(:,1:2) + delta;

    dist = pdist2(aligned, M2(:,1:2));
    matches = sum(min(dist,[],2) < 8);
    best = max(best, matches);
    
    if matches > best
        bestDelta = delta;
    end
end

score = best / min(size(M1,1), size(M2,1));
end

function minutiae = extractMinutiae(S)
minutiae = [];
[h, w] = size(S);
for i = 2:h-1
    for j = 2:w-1
        if ~S(i,j), continue; end
        nb = sum(sum(S(i-1:i+1, j-1:j+1))) - 1;

        if nb == 1  % termination
            type = 1;
        elseif nb == 3 % bifurcation
            type = 2;
        else
            continue;
        end

        minutiae = [minutiae; j, i, type];
    end
end
end

function E = gaborEnhanced(I, O, F, mask)
[h, w] = size(I);
E = zeros(h, w);
blk = 16;

for i = 1:blk:h-blk
    for j = 1:blk:w-blk
        if ~mask(i,j), continue; end

        theta = O(i,j);
        freq  = F(i,j);

        % Sécurité : fréquence minimale
        if isnan(freq) || freq <= 0
            freq = 0.1;  % valeur par défaut raisonnable
        end

        lambda = 1 / freq;

        % Vérifie lambda fini
        if ~isfinite(lambda)
            lambda = 10; % fallback
        end
        lambda = 1 / freq;

        try
            g = gabor(lambda, theta*180/pi, 'SpatialFrequencyBandwidth',1);
           patch = I(i:i+blk-1,j:j+blk-1);
                E(i:i+blk-1,j:j+blk-1) = imgaborfilt(patch,g);
        catch
            % En cas d’erreur, copier le patch original
            E(i:i+blk-1, j:j+blk-1) = I(i:i+blk-1, j:j+blk-1);
        end
    end
end

E = mat2gray(E);
end

function F = ridgeFrequency(I, O)
blk = 16;
[h, w] = size(I);
F = zeros(h, w);

for i = 1:blk:h-blk
    for j = 1:blk:w-blk
        patch = I(i:i+blk-1, j:j+blk-1);
        theta = mean(O(i:i+blk-1, j:j+blk-1), 'all');

        rot = imrotate(patch, -theta*180/pi, 'bilinear', 'crop');
        proj = sum(rot, 2);
        spectrum = abs(fft(proj));
        [~, idx] = max(spectrum(2:end/2));
        freq = idx / blk;

        if freq < 0.05, freq = 0.1; end  % valeur minimale
        F(i:i+blk-1, j:j+blk-1) = freq;
    end
end
end

function O = orientationTensor(I)
[Gx, Gy] = imgradientxy(I);

Jxx = imgaussfilt(Gx.^2, 2);
Jyy = imgaussfilt(Gy.^2, 2);
Jxy = imgaussfilt(Gx.*Gy, 2);

O = 0.5 * atan2(2*Jxy, (Jxx - Jyy));
O = imgaussfilt(O, 3); % lissage
end

function mask = segmentCoherence(I)
[Gx, Gy] = imgradientxy(I);
Jxx = imgaussfilt(Gx.^2, 2);
Jyy = imgaussfilt(Gy.^2, 2);
Jxy = imgaussfilt(Gx.*Gy, 2);

lambda1 = 0.5*((Jxx+Jyy) + sqrt((Jxx-Jyy).^2 + 4*Jxy.^2));
lambda2 = 0.5*((Jxx+Jyy) - sqrt((Jxx-Jyy).^2 + 4*Jxy.^2));

coherence = (lambda1 - lambda2) ./ (lambda1 + lambda2 + eps);

mask = coherence > 0.2; % seuil moderne
mask = bwareaopen(mask, 50);
end


function N = normalizeLocal(I, w)
if nargin < 2, w = 16; end
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
N = targetMean + targetStd * (I - mu) ./ (sigma + eps);
end

function showOrientationField(O, mask)
    [h,w] = size(O);
    step = 16; % résolution
    hold on
    imagesc(O.*mask); colormap('jet'); axis image; axis off;

    for i=1:step:h
        for j=1:step:w
            if ~mask(i,j), continue; end
            theta = O(i,j);
            dx = 8*cos(theta);
            dy = 8*sin(theta);
            plot([j-dx j+dx],[i-dy i+dy],'w','LineWidth',1.2);
        end
    end
    hold off
end

function showMinutiae(S, M)
    imshow(S,[]); hold on;

    for k=1:size(M,1)
        x = M(k,1); y = M(k,2); type = M(k,3);
        if type == 1
            plot(x,y,'ro','MarkerSize',6,'LineWidth',1.5);
        else
            plot(x,y,'go','MarkerSize',6,'LineWidth',1.5);
        end
    end
    hold off
end


