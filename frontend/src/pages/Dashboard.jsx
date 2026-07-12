import React, { useState, useEffect } from 'react';
import API from '../api/client';
import VehicleCard from '../components/VehicleCard';
import PurchaseModal from '../components/PurchaseModal';
import { Search, SlidersHorizontal, History, DollarSign, Calendar, XCircle, ShoppingBag, Download } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const Dashboard = () => {
  const { user } = useAuth();
  const [vehicles, setVehicles] = useState([]);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [historyLoading, setHistoryLoading] = useState(true);
  const [selectedVehicle, setSelectedVehicle] = useState(null);

  // Search state filters
  const [keyword, setKeyword] = useState('');
  const [make, setMake] = useState('');
  const [model, setModel] = useState('');
  const [year, setYear] = useState('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');

  const [successMsg, setSuccessMsg] = useState('');
  const [lastPurchaseId, setLastPurchaseId] = useState('');

  const fetchVehicles = async () => {
    setLoading(true);
    try {
      const params = {};
      if (keyword) params.keyword = keyword;
      if (make) params.make = make;
      if (model) params.model = model;
      if (year) params.year = year;
      if (minPrice) params.minPrice = minPrice;
      if (maxPrice) params.maxPrice = maxPrice;

      const response = await API.get('/api/vehicles', { params });
      setVehicles(response.data);
    } catch (error) {
      console.error("Error fetching vehicles:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchHistory = async () => {
    setHistoryLoading(true);
    try {
      const response = await API.get('/api/purchases/history');
      setHistory(response.data);
    } catch (error) {
      console.error("Error fetching history:", error);
    } finally {
      setHistoryLoading(false);
    }
  };

  useEffect(() => {
    fetchVehicles();
    fetchHistory();
  }, []);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    fetchVehicles();
  };

  const handleClearFilters = () => {
    setKeyword('');
    setMake('');
    setModel('');
    setYear('');
    setMinPrice('');
    setMaxPrice('');
    // Trigger fetch immediately with empty params
    setTimeout(fetchVehicles, 0);
  };

  const handleDownloadInvoice = async (id) => {
    try {
      const response = await API.get(`/api/purchases/${id}/invoice`, {
        responseType: 'blob'
      });
      const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `invoice-${id}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
    } catch (error) {
      console.error("Error downloading invoice:", error);
      alert("Failed to download invoice. Please try again.");
    }
  };

  const handlePurchaseSuccess = (record) => {
    setSelectedVehicle(null);
    setLastPurchaseId(record.id);
    setSuccessMsg(`Successfully purchased vehicle! Transaction ID: ${record.id}`);
    fetchVehicles();
    fetchHistory();
    // Clear success message after 10s
    setTimeout(() => {
      setSuccessMsg('');
      setLastPurchaseId('');
    }, 10000);
  };

  return (
    <div className="max-w-7xl mx-auto px-6 py-8 space-y-10">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-violet-950 to-indigo-900 border border-violet-500/20 rounded-3xl p-8 relative overflow-hidden shadow-xl">
        <div className="absolute top-0 right-0 w-80 h-80 bg-violet-500/10 rounded-full blur-3xl"></div>
        <div className="z-10 relative space-y-2">
          <h1 className="text-3xl font-extrabold text-white">
            Hello, <span className="bg-gradient-to-r from-violet-300 to-indigo-200 bg-clip-text text-transparent">{user}</span>!
          </h1>
          <p className="text-slate-300 max-w-xl text-sm leading-relaxed">
            Welcome to AutoHaven. Browse our premium car selection and complete secure transactions.
          </p>
        </div>
      </div>

      {successMsg && (
        <div className="bg-emerald-500/10 border border-emerald-500/25 text-emerald-400 px-6 py-4 rounded-2xl flex items-center justify-between shadow-lg shadow-emerald-950/20 animate-bounce">
          <div className="flex items-center space-x-2 text-sm font-semibold">
            <ShoppingBag className="h-5 w-5" />
            <span>{successMsg}</span>
          </div>
          <div className="flex items-center space-x-3">
            {lastPurchaseId && (
              <button
                onClick={() => handleDownloadInvoice(lastPurchaseId)}
                className="bg-emerald-600 hover:bg-emerald-500 text-white font-bold py-1.5 px-3 rounded-lg flex items-center space-x-1.5 text-xs transition-colors duration-200 cursor-pointer border-0"
              >
                <Download className="h-3.5 w-3.5" />
                <span>Download Invoice</span>
              </button>
            )}
            <button onClick={() => setSuccessMsg('')} className="text-emerald-450 hover:text-emerald-300 cursor-pointer bg-transparent border-0">
              <XCircle className="h-5 w-5" />
            </button>
          </div>
        </div>
      )}

      {/* Search & Filter Form */}
      <div className="bg-slate-900/50 backdrop-blur-md border border-slate-800 rounded-3xl p-6 shadow-md">
        <div className="flex items-center space-x-2 mb-4 text-slate-350">
          <SlidersHorizontal className="h-5 w-5 text-violet-400" />
          <h2 className="text-lg font-bold text-white">Find Your Next Car</h2>
        </div>

        <form onSubmit={handleSearchSubmit} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {/* Keyword search */}
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-500">
                <Search className="h-4.5 w-4.5" />
              </div>
              <input
                type="text"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                placeholder="Search by make, model..."
                className="w-full pl-11 pr-4 py-2.5 bg-slate-950/40 border border-slate-850 rounded-xl text-slate-300 placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500"
              />
            </div>

            {/* Make filter */}
            <input
              type="text"
              value={make}
              onChange={(e) => setMake(e.target.value)}
              placeholder="Make (e.g. Ford)"
              className="w-full px-4 py-2.5 bg-slate-950/40 border border-slate-850 rounded-xl text-slate-300 placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500"
            />

            {/* Model filter */}
            <input
              type="text"
              value={model}
              onChange={(e) => setModel(e.target.value)}
              placeholder="Model (e.g. Mustang)"
              className="w-full px-4 py-2.5 bg-slate-950/40 border border-slate-850 rounded-xl text-slate-300 placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500"
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            {/* Year filter */}
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-500">
                <Calendar className="h-4 w-4" />
              </div>
              <input
                type="number"
                value={year}
                onChange={(e) => setYear(e.target.value)}
                placeholder="Year"
                className="w-full pl-10 pr-4 py-2.5 bg-slate-950/40 border border-slate-850 rounded-xl text-slate-300 placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500"
              />
            </div>

            {/* Min Price filter */}
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-500">
                <DollarSign className="h-4 w-4" />
              </div>
              <input
                type="number"
                value={minPrice}
                onChange={(e) => setMinPrice(e.target.value)}
                placeholder="Min Price"
                className="w-full pl-10 pr-4 py-2.5 bg-slate-950/40 border border-slate-850 rounded-xl text-slate-300 placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500"
              />
            </div>

            {/* Max Price filter */}
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-500">
                <DollarSign className="h-4 w-4" />
              </div>
              <input
                type="number"
                value={maxPrice}
                onChange={(e) => setMaxPrice(e.target.value)}
                placeholder="Max Price"
                className="w-full pl-10 pr-4 py-2.5 bg-slate-950/40 border border-slate-850 rounded-xl text-slate-300 placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500"
              />
            </div>

            {/* Buttons */}
            <div className="flex gap-2">
              <button
                type="submit"
                className="flex-1 bg-violet-650 hover:bg-violet-500 text-white font-semibold py-2.5 rounded-xl transition-all duration-300 cursor-pointer shadow-md text-sm"
              >
                Search
              </button>
              <button
                type="button"
                onClick={handleClearFilters}
                className="px-4 bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold rounded-xl border border-slate-700 transition-colors duration-200 cursor-pointer text-sm"
              >
                Clear
              </button>
            </div>
          </div>
        </form>
      </div>

      {/* Catalog Vehicles Grid */}
      <div className="space-y-6">
        <h2 className="text-xl font-bold text-white border-l-4 border-violet-500 pl-3">Vehicle Catalog</h2>
        {loading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {[1, 2, 3].map((idx) => (
              <div key={idx} className="bg-slate-900/20 border border-slate-850 rounded-2xl h-[360px] animate-pulse"></div>
            ))}
          </div>
        ) : vehicles.length === 0 ? (
          <div className="bg-slate-900/30 border border-slate-850 rounded-2xl p-12 text-center text-slate-400">
            No vehicles match your search criteria. Try broadening your filters!
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {vehicles.map((v) => (
              <VehicleCard
                key={v.id}
                vehicle={v}
                isAdmin={false}
                onPurchase={setSelectedVehicle}
              />
            ))}
          </div>
        )}
      </div>

      {/* Purchase History */}
      <div className="space-y-6">
        <div className="flex items-center space-x-2 border-l-4 border-violet-500 pl-3">
          <History className="h-5 w-5 text-violet-400" />
          <h2 className="text-xl font-bold text-white">Your Purchase History</h2>
        </div>

        {historyLoading ? (
          <div className="bg-slate-900/20 border border-slate-850 rounded-2xl h-32 animate-pulse"></div>
        ) : history.length === 0 ? (
          <div className="bg-slate-900/30 border border-slate-850 rounded-2xl p-8 text-center text-slate-400 text-sm">
            You haven't purchased any vehicles yet.
          </div>
        ) : (
          <div className="bg-slate-900/40 border border-slate-800 rounded-2xl overflow-hidden shadow-lg">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse text-sm">
                <thead>
                  <tr className="bg-slate-950/40 border-b border-slate-800 text-slate-400 font-semibold">
                    <th className="p-4">Transaction ID</th>
                    <th className="p-4">Vehicle ID</th>
                    <th className="p-4">Price per Unit</th>
                    <th className="p-4">Quantity</th>
                    <th className="p-4">Total Amount</th>
                    <th className="p-4">Date</th>
                    <th className="p-4 text-right">Invoice</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60 text-slate-300">
                  {history.map((record) => (
                    <tr key={record.id} className="hover:bg-slate-850/30 transition-colors duration-200">
                      <td className="p-4 font-mono text-xs text-violet-400">{record.id}</td>
                      <td className="p-4 font-mono text-xs">{record.vehicleId}</td>
                      <td className="p-4">${record.purchasePrice.toLocaleString()}</td>
                      <td className="p-4">{record.quantity}</td>
                      <td className="p-4 font-bold text-white">${record.totalAmount.toLocaleString()}</td>
                      <td className="p-4 text-xs text-slate-400">
                        {new Date(record.purchasedAt).toLocaleString()}
                      </td>
                      <td className="p-4 text-right">
                        <button
                          onClick={() => handleDownloadInvoice(record.id)}
                          className="inline-flex items-center space-x-1 bg-violet-500/10 hover:bg-violet-500/20 text-violet-400 font-semibold px-2.5 py-1.5 rounded-lg border border-violet-500/20 hover:border-violet-500/35 transition-all text-xs cursor-pointer"
                        >
                          <Download className="h-3.5 w-3.5" />
                          <span>PDF</span>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>

      {/* Purchase Modal overlay */}
      {selectedVehicle && (
        <PurchaseModal
          vehicle={selectedVehicle}
          onClose={() => setSelectedVehicle(null)}
          onSuccess={handlePurchaseSuccess}
        />
      )}
    </div>
  );
};

export default Dashboard;
