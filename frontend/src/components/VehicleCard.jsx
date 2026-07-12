import React from 'react';
import { Tag, Calendar, Edit2, RotateCcw, Trash2 } from 'lucide-react';

const VehicleCard = ({ vehicle, onPurchase, onEdit, onRestock, onDelete, isAdmin }) => {
  const isOutOfStock = vehicle.stock <= 0;

  return (
    <div className="bg-slate-900/40 backdrop-blur-md border border-slate-800 rounded-2xl p-5 hover:border-slate-700/80 hover:shadow-xl hover:shadow-violet-600/5 transition-all duration-300 flex flex-col justify-between h-[360px]">
      <div>
        {/* Year and Badge */}
        <div className="flex items-center justify-between mb-3">
          <span className="flex items-center space-x-1 text-xs font-semibold text-violet-400 bg-violet-500/10 px-2.5 py-1 rounded-full border border-violet-500/20">
            <Calendar className="h-3 w-3" />
            <span>{vehicle.year}</span>
          </span>
          <span className={`text-xs font-bold px-2.5 py-1 rounded-full ${
            isOutOfStock ? 'bg-rose-500/15 text-rose-400 border border-rose-500/20' : 'bg-emerald-500/15 text-emerald-400 border border-emerald-500/20'
          }`}>
            {isOutOfStock ? 'Out of Stock' : `${vehicle.stock} Available`}
          </span>
        </div>

        {/* Title */}
        <h3 className="text-lg font-bold text-white mb-1 group-hover:text-violet-400 transition-colors duration-200 truncate">
          {vehicle.make} <span className="font-medium text-slate-300">{vehicle.model}</span>
        </h3>

        {/* Price */}
        <div className="flex items-center text-slate-300 font-bold text-xl mb-3">
          <Tag className="h-4 w-4 mr-1 text-violet-400" />
          <span>${vehicle.price.toLocaleString()}</span>
        </div>

        {/* Description */}
        <p className="text-slate-400 text-xs leading-relaxed line-clamp-4 mb-4">
          {vehicle.description || 'No description available for this vehicle model.'}
        </p>
      </div>

      {/* Action Buttons */}
      <div className="pt-4 border-t border-slate-800/80">
        {isAdmin ? (
          <div className="grid grid-cols-3 gap-2">
            <button
              onClick={() => onEdit(vehicle)}
              className="flex items-center justify-center space-x-1 bg-slate-800 hover:bg-slate-700 text-slate-200 py-2 rounded-xl text-xs font-semibold border border-slate-700 cursor-pointer transition-colors duration-200"
              title="Edit Vehicle"
            >
              <Edit2 className="h-3.5 w-3.5" />
              <span>Edit</span>
            </button>
            <button
              onClick={() => onRestock(vehicle)}
              className="flex items-center justify-center space-x-1 bg-violet-600/10 hover:bg-violet-600/20 text-violet-400 py-2 rounded-xl text-xs font-semibold border border-violet-500/20 cursor-pointer transition-colors duration-200"
              title="Restock Inventory"
            >
              <RotateCcw className="h-3.5 w-3.5" />
              <span>Stock</span>
            </button>
            <button
              onClick={() => onDelete(vehicle.id)}
              className="flex items-center justify-center space-x-1 bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 py-2 rounded-xl text-xs font-semibold border border-rose-500/20 cursor-pointer transition-colors duration-200"
              title="Delete Vehicle"
            >
              <Trash2 className="h-3.5 w-3.5" />
              <span>Delete</span>
            </button>
          </div>
        ) : (
          <button
            onClick={() => onPurchase(vehicle)}
            disabled={isOutOfStock}
            className={`w-full py-2.5 rounded-xl text-sm font-semibold transition-all duration-300 cursor-pointer flex items-center justify-center space-x-1.5 ${
              isOutOfStock
                ? 'bg-slate-800 text-slate-500 border border-slate-700/50 cursor-not-allowed'
                : 'bg-violet-600 hover:bg-violet-500 text-white shadow-lg shadow-violet-600/10 active:scale-98'
            }`}
          >
            <span>Purchase Vehicle</span>
          </button>
        )}
      </div>
    </div>
  );
};

export default VehicleCard;
